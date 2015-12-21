FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI += "file://functions"

ENABLE_CONFIGS = "PGREP ADDUSER STAT"

do_configure_prepend() {
	for c in ${ENABLE_CONFIGS}; do
		sed -i "s/# CONFIG_${c} is not set/CONFIG_${c}=y/" ${WORKDIR}/defconfig || true
	done
}

do_install_append() {
	install -d ${D}/${sysconfdir}/init.d
	install -m 0644 ${WORKDIR}/functions ${D}${sysconfdir}/init.d/
}
