SUMMARY = "Smart Home Monitor"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://README;md5=31d61b7becaa03bd5bfddf7a0f2edd2d"

SRC_URI = "file://smarthome-monitor_${PV}.tgz \
	   file://smarthome-monitor-client.service \
	   file://smarthome-monitor-server.service "

SRC_URI[md5sum] = "9eca20e05044f4e245ffb4377f3a8656"
SRC_URI[sha256sum] = "464053e07b98982883b2364a50ac85137d8dad7b7d86da74d29d1af1af0a37af"

RDEPENDS_${PN} = "lib32-iotivity-nodejs"

INSANE_SKIP_${PN} += "installed-vs-shipped"

PREFIX = "/opt/smarthome-monitor"

inherit gettext

do_install() {
	install -d ${D}/${PREFIX}-client ${D}/${PREFIX}-server
	install -d ${D}${systemd_unitdir}/system

	install -m 644  ${S}/config.js ${S}/iotclient.js ${D}/${PREFIX}-client
	cp -rfp  ${S}/public ${S}/views ${S}/routes ${S}/node_modules ${D}/${PREFIX}-client
	install -m 0644 ${WORKDIR}/smarthome-monitor-client.service ${D}${systemd_unitdir}/system
	sed -i 's|@PREFIX@|${PREFIX}|g' ${D}${systemd_unitdir}/system/smarthome-monitor-client.service 

	install -m 644  ${S}/config.js ${S}/iotserver.js ${D}/${PREFIX}-server
	cp -rfp ${S}/node_modules ${D}/${PREFIX}-server
	install -m 0644 ${WORKDIR}/smarthome-monitor-server.service ${D}${systemd_unitdir}/system
	sed -i 's|@PREFIX@|${PREFIX}|g' ${D}${systemd_unitdir}/system/smarthome-monitor-server.service 
} 

SYSTEMD_SERVICE_${PN}-client = "smarthome-monitor-client.service"
SYSTEMD_SERVICE_${PN}-server = "smarthome-monitor-server.service"

PACKAGES = "${PN}-client ${PN}-server"

FILES_${PN}-client = "${PREFIX}-client ${systemd_unitdir}/system/smarthome-monitor-client.service"
FILES_${PN}-server = "${PREFIX}-server ${systemd_unitdir}/system/smarthome-monitor-server.service"
