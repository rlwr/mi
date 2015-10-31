SUMMARY = "Iotivity Nodejs Wrapper"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://README.md;md5=000bb091ea33068879903ef3f59d0b1c"
SRC_URI = "file://iotivity-nodejs_${PV}.tgz"
SRC_URI[md5sum] = "c427217375d892c8f22bb4380e72b796"
SRC_URI[sha256sum] = "575b12ad4cf45c61b2ea61cb741c74b67cd854dbb90a5822c01684d178d3d417"

DEPENDS = "iotivity nodejs-native"
RDEPENDS_${PN} = "iotivity"

INSANE_SKIP_${PN} += "installed-vs-shipped"

inherit gettext

do_configure() {
	sed 's|@incdir@|${STAGING_INCDIR}|g' -i ${S}/binding.gyp
	sed 's|@libdir@|${STAGING_LIBDIR}|g' -i ${S}/binding.gyp
	cd ${S} && ${STAGING_LIBDIR_NATIVE}/node_modules/npm/bin/node-gyp-bin/node-gyp configure
}

do_compile() {
	cd ${S} && ${STAGING_LIBDIR_NATIVE}/node_modules/npm/bin/node-gyp-bin/node-gyp build
}

do_install() {
	install -d ${D}/usr/lib/node_modules/iotivity/
	install -m 644 ${S}/build/Release/iotivity.node ${D}/usr/lib/node_modules/iotivity/
	install -m 644 ${S}/index.js ${S}/iotclient.js ${S}/iotserver.js ${D}/usr/lib/node_modules/iotivity/
} 
