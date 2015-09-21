include chromium.inc

SRC_URI += "\
        ${@bb.utils.contains('PACKAGECONFIG', 'ignore-lost-context', 'file://chromium-37/0001-Remove-accelerated-Canvas-support-from-blacklist.patch', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'impl-side-painting', 'file://chromium-37/0002-Disable-rasterization-whitelist-unlocking-impl-side-.patch', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'disable-api-keys-info-bar', 'file://chromium-37/0003-Disable-API-keys-info-bar.patch', '', d)} \
        file://chromium-37/unistd-2.patch \
        file://chromium-37/0004-Shell-integration-conditionally-compile-routines-for.patch \
"
SRC_URI[md5sum] = "49bcf221a2e2e5406ae2e69964d01093"
SRC_URI[sha256sum] = "d27c19580b74cbe143131f0bc097557b3b2fb3d2be966e688d8af51a779ce533"

CFLAGS_append = " -Wno-sign-compare "
CXXFLAGS_append = " -Wno-sign-compare -D__STDC_CONSTANT_MACROS  "

PACKAGECONFIG += " use-egl disable-unwanted-background-traffic remove-badflags-warnings "
