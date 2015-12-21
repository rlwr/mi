
#RDEPENDS_${PN} += "cryptsetup tpm-tools trousers luksipc tcgbox shadow e2fsprogs"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://encrypt-root-support.patch;striplevel=0;md5sum=49bd789b68bc5ee065f553626f3fea1d"


