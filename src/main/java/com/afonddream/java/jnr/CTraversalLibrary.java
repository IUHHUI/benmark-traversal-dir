package com.afonddream.java.jnr;

import jnr.ffi.annotations.In;
import jnr.ffi.types.int32_t;
import jnr.ffi.types.uintptr_t;

public interface CTraversalLibrary {
    @int32_t
    int traversal_single_threaded(@In @uintptr_t String path,
                                  @In @uintptr_t String output,
                                  @In @uintptr_t String suffix_matcher);

    @int32_t
    int traversal_multi_threaded(@In @uintptr_t String path,
                                 @In @uintptr_t String output,
                                 @In @uintptr_t String suffix_matcher);
}