RDEPENDS_${PN} = ""

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://tcsd_initramfs.patch;striplevel=0;patchdir=.."

inherit initramfs-rc
INITRAMFS_SCRIPT = "${WORKDIR}/tcsd"

