include chromium.inc

SRC_URI += "\
        ${@bb.utils.contains('PACKAGECONFIG', 'impl-side-painting', 'file://chromium-45/0002-Add-Linux-to-impl-side-painting-whitelist.patch', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'disable-api-keys-info-bar', 'file://chromium-45/0003-Disable-API-keys-info-bar.patch', '', d)} \
        file://chromium-45/0004-Remove-hard-coded-values-for-CC-and-CXX.patch \
        file://chromium-45/unistd-2.patch  \
"
SRC_URI[md5sum] = "f1b9c46940cefada831d642ec98f95c4"
SRC_URI[sha256sum] = "15d1a31fd0acfca07d614249518192983890507641e09db8d4c91d9ddf7ea340"

PACKAGECONFIG += " enable-hardware-acceleration use-egl disable-unwanted-background-traffic remove-badflags-warnings ignore-lost-context ignore-lost-context"

CHROMIUM_EXTRA_ARGS += " --no-sandbox --enable-experimental-extension-apis --javascript-harmony "
