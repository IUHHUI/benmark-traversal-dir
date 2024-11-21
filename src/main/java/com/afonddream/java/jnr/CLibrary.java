package com.afonddream.java.jnr;

import jnr.ffi.Pointer;
import jnr.ffi.annotations.In;
import jnr.ffi.types.int32_t;
import jnr.ffi.types.uintptr_t;

public interface CLibrary {
    @uintptr_t
    Pointer opendir(@In @uintptr_t String name);

    @uintptr_t
    Pointer readdir(@In @uintptr_t Pointer dirp);

    @int32_t
    int closedir(@In @uintptr_t Pointer dirp);
}