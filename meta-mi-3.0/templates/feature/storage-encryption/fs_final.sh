#!/bin/sh

# Exit if this is not initramfs
echo "${IMAGE_ROOTFS}" | grep -q initramfs || exit 0

# Remove from initramfs some of the unnecessary files brought in by RDEPENDS packages
rm -fv  usr/bin/perl*
rm -fv  usr/bin/ajs*
rm -fv  usr/bin/sqlite3
rm -fv  usr/sbin/lv*
rm -fv  usr/sbin/vg*
rm -fv  usr/sbin/userdel
rm -fv  usr/sbin/groupdel
rm -fvr usr/lib/appweb
rm -fvr usr/lib*/perl
rm -fv  usr/lib*/libperl*
rm -fvr usr/share/doc
rm -fvr usr/share/readline
rm -fvr usr/include
rm -fv  lib*/libsqlite3*
find -xtype l -delete

# Add localhost to etc/hosts

echo "127.0.0.1		localhost" >  etc/hosts
