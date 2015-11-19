SUMMARY = "Iotivity Nodejs Wrapper"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://README.md;md5=000bb091ea33068879903ef3f59d0b1c"
SRC_URI = "file://iotivity-nodejs_${PV}.tgz"
SRC_URI[md5sum] = "5cbec2697b0ec75feec1e3b89d93e0e4"
SRC_URI[sha256sum] = "5759997bb9f4157ed0f08ea6e2c987b92eb90a76f116f8c4fb603cd97b37dd35"

DEPENDS = "iotivity nodejs-native"
RDEPENDS_${PN} = "iotivity-resource"


NODE_GYP_ARCH_i586   = " --arch=ia32 "
NODE_GYP_ARCH_x86-64 = " --arch=x64 "

INSANE_SKIP_${PN} += "installed-vs-shipped"

inherit gettext

do_configure() {
	sed 's|@incdir@|${STAGING_INCDIR}|g' -i ${S}/binding.gyp
	sed 's|@libdir@|${STAGING_LIBDIR}|g' -i ${S}/binding.gyp
	cd ${S} && ${STAGING_LIBDIR_NATIVE}/node_modules/npm/bin/node-gyp-bin/node-gyp configure ${NODE_GYP_ARCH}
}

do_compile() {
	cd ${S} && ${STAGING_LIBDIR_NATIVE}/node_modules/npm/bin/node-gyp-bin/node-gyp - build
}

do_install() {
	install -d ${D}/usr/lib/node_modules/iotivity_client/
	install -m 644 ${S}/build/Release/iotivity_client.node ${D}/usr/lib/node_modules/iotivity_client/
	install -m 644 ${S}/iotclient.js  ${D}/usr/lib/node_modules/iotivity_client/
	install -m 644 ${S}/client.js  ${D}/usr/lib/node_modules/iotivity_client/index.js

	install -d ${D}/usr/lib/node_modules/iotivity_server/
	install -m 644 ${S}/build/Release/iotivity_server.node ${D}/usr/lib/node_modules/iotivity_server/
	install -m 644 ${S}/iotserver.js  ${D}/usr/lib/node_modules/iotivity_server/
	install -m 644 ${S}/server.js  ${D}/usr/lib/node_modules/iotivity_server/index.js

} 


FILES_${PN}  = "/usr/lib/node_modules/iotivity_client/"
FILES_${PN} += "/usr/lib/node_modules/iotivity_server/"
