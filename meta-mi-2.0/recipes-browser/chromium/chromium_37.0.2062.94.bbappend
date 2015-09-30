FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://chromium-37/gcc-4.7.patch"

CFLAGS_append = " -Wno-sign-compare "
CXXFLAGS_append = " -Wno-sign-compare -D__STDC_CONSTANT_MACROS -D__STDC_LIMIT_MACROS "

PACKAGECONFIG += " component-build "
