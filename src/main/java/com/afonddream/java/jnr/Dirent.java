package com.afonddream.java.jnr;

import jnr.ffi.Runtime;
import jnr.ffi.Struct;

public class Dirent extends Struct {
    public Signed64 dino = new Signed64();       // ino_t
    public Signed64 off = new Signed64();       // off_t
    public Unsigned16 dreclen = new Unsigned16();   // unsigned short
    public BYTE type = new BYTE();      // unsigned char
    public UTF8String dname = new UTF8String(256);

    public Dirent(Runtime runtime) {
        super(runtime);
    }

    public java.lang.String getDName() {
        return dname.get();
    }

    public boolean isRegularFile() {
        return type.get() == 8; // DT_REG
    }

    public boolean isDirectory() {
        return type.get() == 4; // DT_DIR
    }

    public boolean isSymbolicLink() {
        return type.get() == 10; // DT_LNK
    }
}
