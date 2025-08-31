#!/usr/bin/env python3
"""
Filesystem Sandbox Server - Python service for file operations
Provides basic file system operations through HTTP API
"""

import os
import sys
import json
import shutil
import traceback
from typing import List, Dict, Any, Optional
from pathlib import Path

from fastapi import FastAPI, HTTPException, Depends, Security
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import uvicorn

# Initialize FastAPI app
app = FastAPI(
    title="AgentScope Filesystem Sandbox",
    description="Python service for file system operations in sandbox environment",
    version="1.0.0"
)

# CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Security
security = HTTPBearer(auto_error=False)

# Configuration
WORKSPACE_DIR = os.getenv("WORKSPACE_DIR", "/workspace")
SESSION_ID = os.getenv("SESSION_ID", "default")
SECRET_TOKEN = os.getenv("SECRET_TOKEN", "")


class FileOperationResult(BaseModel):
    """Result of file operation"""
    success: bool
    message: str
    data: Optional[Dict[str, Any]] = None
    error: Optional[str] = None


class ReadFileRequest(BaseModel):
    """Request to read file"""
    path: str


class WriteFileRequest(BaseModel):
    """Request to write file"""
    path: str
    content: str


class CreateDirectoryRequest(BaseModel):
    """Request to create directory"""
    path: str


class ListDirectoryRequest(BaseModel):
    """Request to list directory"""
    path: str


class MoveFileRequest(BaseModel):
    """Request to move/rename file"""
    source_path: str
    destination_path: str


class GetFileInfoRequest(BaseModel):
    """Request to get file info"""
    path: str


def verify_token(credentials: Optional[HTTPAuthorizationCredentials] = Security(security)):
    """Verify authentication token"""
    if not SECRET_TOKEN:
        return True  # No token required if not configured
    
    if not credentials:
        raise HTTPException(status_code=401, detail="Authentication required")
    
    if credentials.credentials != SECRET_TOKEN:
        raise HTTPException(status_code=401, detail="Invalid token")
    
    return True


def get_safe_path(path: str) -> Path:
    """Get safe path within workspace"""
    # Resolve path relative to workspace
    if path.startswith('/'):
        # Absolute path - make it relative to workspace
        path = path.lstrip('/')
    
    safe_path = Path(WORKSPACE_DIR) / path
    safe_path = safe_path.resolve()
    
    # Ensure path is within workspace
    workspace_path = Path(WORKSPACE_DIR).resolve()
    if not str(safe_path).startswith(str(workspace_path)):
        raise HTTPException(status_code=403, detail="Access denied: path outside workspace")
    
    return safe_path


@app.get("/healthz")
async def health_check():
    """Health check endpoint"""
    return "OK"


@app.get("/health")
async def detailed_health():
    """Detailed health status"""
    return {
        "status": "healthy",
        "session_id": SESSION_ID,
        "workspace_dir": WORKSPACE_DIR,
        "python_version": sys.version,
        "service": "filesystem-sandbox"
    }


@app.post("/tools/read_file", response_model=FileOperationResult)
async def read_file(
    request: ReadFileRequest,
    _: bool = Depends(verify_token)
) -> FileOperationResult:
    """Read file content"""
    try:
        file_path = get_safe_path(request.path)
        
        if not file_path.exists():
            return FileOperationResult(
                success=False,
                message="File not found",
                error=f"File does not exist: {request.path}"
            )
        
        if not file_path.is_file():
            return FileOperationResult(
                success=False,
                message="Path is not a file",
                error=f"Path is not a file: {request.path}"
            )
        
        content = file_path.read_text(encoding='utf-8')
        
        return FileOperationResult(
            success=True,
            message="File read successfully",
            data={"content": content, "path": request.path}
        )
        
    except Exception as e:
        error_msg = f"Error reading file: {str(e)}\n{traceback.format_exc()}"
        return FileOperationResult(
            success=False,
            message="Failed to read file",
            error=error_msg
        )


@app.post("/tools/write_file", response_model=FileOperationResult)
async def write_file(
    request: WriteFileRequest,
    _: bool = Depends(verify_token)
) -> FileOperationResult:
    """Write file content"""
    try:
        file_path = get_safe_path(request.path)
        
        # Create parent directories if they don't exist
        file_path.parent.mkdir(parents=True, exist_ok=True)
        
        file_path.write_text(request.content, encoding='utf-8')
        
        return FileOperationResult(
            success=True,
            message="File written successfully",
            data={"path": request.path, "size": len(request.content)}
        )
        
    except Exception as e:
        error_msg = f"Error writing file: {str(e)}\n{traceback.format_exc()}"
        return FileOperationResult(
            success=False,
            message="Failed to write file",
            error=error_msg
        )


@app.post("/tools/create_directory", response_model=FileOperationResult)
async def create_directory(
    request: CreateDirectoryRequest,
    _: bool = Depends(verify_token)
) -> FileOperationResult:
    """Create directory"""
    try:
        dir_path = get_safe_path(request.path)
        
        if dir_path.exists():
            if dir_path.is_dir():
                return FileOperationResult(
                    success=True,
                    message="Directory already exists",
                    data={"path": request.path}
                )
            else:
                return FileOperationResult(
                    success=False,
                    message="Path exists but is not a directory",
                    error=f"Path exists but is not a directory: {request.path}"
                )
        
        dir_path.mkdir(parents=True, exist_ok=True)
        
        return FileOperationResult(
            success=True,
            message="Directory created successfully",
            data={"path": request.path}
        )
        
    except Exception as e:
        error_msg = f"Error creating directory: {str(e)}\n{traceback.format_exc()}"
        return FileOperationResult(
            success=False,
            message="Failed to create directory",
            error=error_msg
        )


@app.post("/tools/list_directory", response_model=FileOperationResult)
async def list_directory(
    request: ListDirectoryRequest,
    _: bool = Depends(verify_token)
) -> FileOperationResult:
    """List directory contents"""
    try:
        dir_path = get_safe_path(request.path)
        
        if not dir_path.exists():
            return FileOperationResult(
                success=False,
                message="Directory not found",
                error=f"Directory does not exist: {request.path}"
            )
        
        if not dir_path.is_dir():
            return FileOperationResult(
                success=False,
                message="Path is not a directory",
                error=f"Path is not a directory: {request.path}"
            )
        
        items = []
        for item in dir_path.iterdir():
            items.append({
                "name": item.name,
                "type": "directory" if item.is_dir() else "file",
                "size": item.stat().st_size if item.is_file() else None,
                "path": str(item.relative_to(Path(WORKSPACE_DIR)))
            })
        
        return FileOperationResult(
            success=True,
            message="Directory listed successfully",
            data={"path": request.path, "items": items}
        )
        
    except Exception as e:
        error_msg = f"Error listing directory: {str(e)}\n{traceback.format_exc()}"
        return FileOperationResult(
            success=False,
            message="Failed to list directory",
            error=error_msg
        )


@app.post("/tools/move_file", response_model=FileOperationResult)
async def move_file(
    request: MoveFileRequest,
    _: bool = Depends(verify_token)
) -> FileOperationResult:
    """Move/rename file or directory"""
    try:
        source_path = get_safe_path(request.source_path)
        dest_path = get_safe_path(request.destination_path)
        
        if not source_path.exists():
            return FileOperationResult(
                success=False,
                message="Source path not found",
                error=f"Source path does not exist: {request.source_path}"
            )
        
        if dest_path.exists():
            return FileOperationResult(
                success=False,
                message="Destination path already exists",
                error=f"Destination path already exists: {request.destination_path}"
            )
        
        # Create parent directories if they don't exist
        dest_path.parent.mkdir(parents=True, exist_ok=True)
        
        shutil.move(str(source_path), str(dest_path))
        
        return FileOperationResult(
            success=True,
            message="File moved successfully",
            data={
                "source_path": request.source_path,
                "destination_path": request.destination_path
            }
        )
        
    except Exception as e:
        error_msg = f"Error moving file: {str(e)}\n{traceback.format_exc()}"
        return FileOperationResult(
            success=False,
            message="Failed to move file",
            error=error_msg
        )


@app.post("/tools/get_file_info", response_model=FileOperationResult)
async def get_file_info(
    request: GetFileInfoRequest,
    _: bool = Depends(verify_token)
) -> FileOperationResult:
    """Get file or directory information"""
    try:
        file_path = get_safe_path(request.path)
        
        if not file_path.exists():
            return FileOperationResult(
                success=False,
                message="Path not found",
                error=f"Path does not exist: {request.path}"
            )
        
        stat = file_path.stat()
        
        info = {
            "path": request.path,
            "name": file_path.name,
            "type": "directory" if file_path.is_dir() else "file",
            "size": stat.st_size,
            "modified_time": stat.st_mtime,
            "created_time": stat.st_ctime,
            "permissions": oct(stat.st_mode)[-3:]
        }
        
        return FileOperationResult(
            success=True,
            message="File info retrieved successfully",
            data=info
        )
        
    except Exception as e:
        error_msg = f"Error getting file info: {str(e)}\n{traceback.format_exc()}"
        return FileOperationResult(
            success=False,
            message="Failed to get file info",
            error=error_msg
        )


@app.on_event("startup")
async def startup_event():
    """Initialize on startup"""
    # Ensure workspace directory exists
    Path(WORKSPACE_DIR).mkdir(parents=True, exist_ok=True)
    
    # Change to workspace directory
    os.chdir(WORKSPACE_DIR)
    
    print(f"Filesystem sandbox server started for session: {SESSION_ID}")
    print(f"Workspace directory: {WORKSPACE_DIR}")
    print(f"Python version: {sys.version}")


if __name__ == "__main__":
    # Run the server
    uvicorn.run(
        app,
        host="0.0.0.0",
        port=8000,
        log_level="info"
    )