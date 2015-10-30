SUMMARY = "Iotivity Nodejs Wrapper"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://README.md;md5=000bb091ea33068879903ef3f59d0b1c"
SRC_URI = "file://oic-js-wrapper_${PV}.tgz"
SRC_URI[md5sum] = "a1ce34f14295e21954eb6965c43417fd"
SRC_URI[sha256sum] = "9945cc2b3c9f35e94fe994e6c7130e91dd9e0fee72fee426e2864747c86c6dc4"

DEPENDS = "nodejs-native"
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
