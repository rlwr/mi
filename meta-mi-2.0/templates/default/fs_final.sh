#!/bin/sh

# If we are populating an initramfs, do nothing
echo "${IMAGE_ROOTFS}"|grep -q initramfs && exit 0

# Change the default runlevel from 3 to 5 to enable graphics support on startup
# This is the default behaviour of the system. You can change the way to start
# X using customized ways instead of this.
[ -e etc/inittab ] && sed -i "s/id:3:initdefault:/id:5:initdefault:/" etc/inittab
