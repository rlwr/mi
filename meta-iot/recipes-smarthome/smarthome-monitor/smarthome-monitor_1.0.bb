SUMMARY = "Smart Home Monitor"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://README;md5=31d61b7becaa03bd5bfddf7a0f2edd2d"

SRC_URI = "file://smarthome-monitor_${PV}.tgz \
	   file://smarthome-monitor-client.service \
	   file://smarthome-monitor-server.service "

SRC_URI[md5sum] = "6ce877fd1dfe32f636b203b46b25fa00"
SRC_URI[sha256sum] = "d61e2943bf5be52875bbd0a48053d7019cc9f517ada4065b788b894c5ffbc413"

RDEPENDS_${PN} = "iotivity-nodejs"

INSANE_SKIP_${PN} += "installed-vs-shipped"

PREFIX = "/opt/smarthome-monitor"

inherit gettext

do_install() {
	install -d ${D}/${PREFIX}-client ${D}/${PREFIX}-server
	install -d ${D}${systemd_unitdir}/system

	install -m 644  ${S}/config.js ${S}/iotclient.js ${D}/${PREFIX}-client
	cp -rfp  ${S}/public ${S}/views ${S}/routes ${S}/node_modules ${D}/${PREFIX}-client
	install -m 0644 ${WORKDIR}/smarthome-monitor-client.service ${D}${systemd_unitdir}/system

	install -m 644  ${S}/config.js ${S}/iotserver.js ${D}/${PREFIX}-server
	cp -rfp ${S}/node_modules ${D}/${PREFIX}-server
	install -m 0644 ${WORKDIR}/smarthome-monitor-server.service ${D}${systemd_unitdir}/system
} 

PACKAGES = "${PN}-client ${PN}-server"

FILES_${PN}-client = "${PREFIX}-client ${systemd_unitdir}/system/smarthome-monitor-client.service"
FILES_${PN}-server = "${PREFIX}-server ${systemd_unitdir}/system/smarthome-monitor-server.service"
