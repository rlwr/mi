include chromium.inc

SRC_URI += "\
        ${@bb.utils.contains('PACKAGECONFIG', 'ignore-lost-context', 'file://chromium-43/0001-Remove-accelerated-Canvas-support-from-blacklist.patch', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'impl-side-painting', 'file://chromium-43/0002-Add-Linux-to-impl-side-painting-whitelist.patch', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'disable-api-keys-info-bar', 'file://chromium-43/0003-Disable-API-keys-info-bar.patch', '', d)} \
        file://chromium-43/0004-Remove-hard-coded-values-for-CC-and-CXX.patch \
        file://chromium-43/unistd-2.patch \
"

SRC_URI[md5sum] = "aba8a1b9945d2c0f203294e43b68cdab"
SRC_URI[sha256sum] = "405f52c6649f1d2937952fbcfcd238ba058db7d13edf4705f7027805f3ce1809"

PACKAGECONFIG += " enable-hardware-acceleration use-egl disable-unwanted-background-traffic remove-badflags-warnings ignore-lost-context ignore-lost-context"

CHROMIUM_EXTRA_ARGS += " --no-sandbox --enable-experimental-extension-apis --javascript-harmony "
