DESCRIPTION = "LUKS In-Place Conversion Tool"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7702f203b58979ebbc31bfaeb44f219c"

SRC_URI = "http://www.johannes-bauer.com/linux/luksipc/luksipc-0.04.tar.gz \
	   file://build-fix.patch"
SRC_URI[md5sum] = "165ac804b534e4294e71df42350ad52e"
SRC_URI[sha256sum] = "2769e83c88231f9689c4a0abcdfd0876b095b8217bf907ec4ef502a03007a353"

S = "${WORKDIR}/${PN}-${PV}"
CFLAGS += "-Wall -Wextra -Wshadow -Wpointer-arith -Wcast-qual -Wstrict-prototypes \
	   -Wmissing-prototypes -std=c99 -D_LARGEFILE64_SOURCE -D_XOPEN_SOURCE=500 \
	   -D__USE_FILE_OFFSET64"

do_compile() {
  oe_runmake
}

do_install() {
  install -d ${D}${bindir}
  install -m 0755 luksipc ${D}${bindir}
}
