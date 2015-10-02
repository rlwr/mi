DESCRIPTION = "Chromium browser"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=537e0b52077bf0a616d0a0c8a79bc9d5"

DEPENDS = "xz-native pciutils pulseaudio cairo nss zlib-native cups ninja-native gconf libexif pango libdrm \
           zlib libdrm libpng jpeg libusb libxslt expat flac libevent speex virtual/libgles2 virtual/egl libva libva-intel-driver ghostscript"

RDEPENDS_${PN} += "libgles2-mesa libegl-mesa libexif libva libva-intel-driver cups ghostscript"


FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI = "\
        http://gsdview.appspot.com/chromium-browser-official/${P}.tar.xz \
        file://include.gypi \
        ${@bb.utils.contains('PACKAGECONFIG', 'component-build', 'file://component-build.gypi', '', d)} \
        file://google-chrome \
        file://google-chrome.desktop \
	file://chromium.gypi \
	file://google-api-key.gypi "


#
# * use-egl : Without this packageconfig, the Chromium build will use GLX for creating an OpenGL context in X11,
#             and regular OpenGL for painting operations. Neither are desirable on embedded platforms. With this
#             packageconfig, EGL and OpenGL ES 2.x are used instead. On by default.
#
# * disable-api-keys-info-bar : This disables the info bar that warns: "Google API keys are missing". With some
#                               builds, missing API keys are considered OK, so the bar needs to go.
#                               Off by default.
#
# * component-build : Enables component build mode. By default, all of Chromium (with the exception of FFmpeg)
#                     is linked into one big binary. The linker step requires at least 8 GB RAM. Component mode
#                     was created to facilitate development and testing, since with it, there is not one big
#                     binary; instead, each component is linked to a separate shared object.
#                     Use component mode for development, testing, and in case the build machine is not a 64-bit
#                     one, or has less than 8 GB RAM. Off by default.
#
# * ignore-lost-context : There is a flaw in the HTML Canvas specification. When the canvas' backing store is
#                         some kind of hardware resource like an OpenGL texture, this resource might get lost.
#                         In case of OpenGL textures, this happens when the OpenGL context gets lost. The canvas
#                         should then be repainted, but nothing in the Canvas standard reflects that.
#                         This packageconfig is to be used if the underlying OpenGL (ES) drivers do not lose
#                         the context, or if losing the context is considered okay (note that canvas contents can
#                         vanish then). Off by default.
#
# * impl-side-painting : This is a new painting mechanism. Still in development stages, it can improve performance.
#                        See http://www.chromium.org/developers/design-documents/impl-side-painting for more.
#                        Off by default.

ENABLE_X11 = "${@base_contains('DISTRO_FEATURES', 'x11', '1', '0', d)}"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE_i586 = "(.*)"
COMPATIBLE_MACHINE_x86-64 = "(.*)"
COMPATIBLE_MACHINE_armv6 = "(.*)"
COMPATIBLE_MACHINE_armv7a = "(.*)"

CHROMIUM_BUILD_TYPE = "Release"

inherit gettext pythonnative

# Avoid OOM during final linkage 
LDFLAGS += " -Wl,--no-keep-memory "

CFLAGS_append = " -fPIC "

PACKAGECONFIG ??= "use-egl"

# this makes sure the dependencies for the EGL mode are present; otherwise, the configure scripts
# automatically and silently fall back to GLX
PACKAGECONFIG[use-egl] = ",,virtual/egl virtual/libgles2"

# Additional PACKAGECONFIG options - listed here to avoid warnings
PACKAGECONFIG[component-build] = ""
PACKAGECONFIG[disable-api-keys-info-bar] = ""
PACKAGECONFIG[ignore-lost-context] = ""
PACKAGECONFIG[impl-side-painting] = ""
PACKAGECONFIG[use-gl] = ""

GYP_ARCH_DEFINES_i586 = " target_arch=ia32"
GYP_ARCH_DEFINES_x86-64 = " target_arch=x64"

# Avoid passing switches incompatible with gcc 4.6
GYP_DEFINES += " ${GYP_ARCH_DEFINES} release_extra_cflags='' sysroot='' "

EXTRA_OEGYP =	" \
	-Dangle_use_commit_id=0 \ 
	-Dclang=0 \
	-Dhost_clang=0 \
	-Ddisable_fatal_linker_warnings=1 \
	${@base_contains('DISTRO_FEATURES', 'ld-is-gold', '', '-Dlinux_use_gold_binary=0', d)} \
	${@base_contains('DISTRO_FEATURES', 'ld-is-gold', '', '-Dlinux_use_gold_flags=0', d)} \
	-I ${WORKDIR}/chromium.gypi \
	${@bb.utils.contains('PACKAGECONFIG', 'component-build', '-I ${WORKDIR}/component-build.gypi', '', d)} \
	-f ninja \
"

CHROMIUM_EXTRA_ARGS = " \
	${@bb.utils.contains('PACKAGECONFIG', 'use-egl', '--use-gl=egl', '', d)} \
	${@bb.utils.contains('PACKAGECONFIG', 'ignore-lost-context', '--gpu-no-context-lost', '', d)} \
	${@bb.utils.contains('PACKAGECONFIG', 'impl-side-painting', '--enable-gpu-rasterization --enable-impl-side-painting', '', d)} \
	${@bb.utils.contains('PACKAGECONFIG', 'disable-unwanted-background-traffic', ' --disable-background-networking --disable-component-extensions-with-background-pages --dns-prefetch-disable ' , '', d)} \
	${@bb.utils.contains('PACKAGECONFIG', 'remove-badflags-warnings', ' --test-type ' , '', d)} \
	${@bb.utils.contains('PACKAGECONFIG', 'enable-hardware-acceleration', ' --enable-gpu --enable-gpu-compositing --force-gpu-rasterization --enable-gpu-sandbox \
	--enable-gpu-sandbox --enable-accelerated-video-decode --ignore-gpu-blacklist --enable-threaded-compositing --enable-threaded-compositing --disable-gpu-vsync --enable-webrtc-hw-decoding \
	--enable-accelerated-2d-canvas --enable-accelerated-jpeg-decoding --enable-webgl --enable-webrtc --enable-deferred-image-decoding ' , '', d)} \
"

python() {
    if d.getVar('ENABLE_X11', True) == '1':
        d.appendVar('DEPENDS', ' xextproto gtk+ libxi libxss ')
}

do_configure() {
	cd ${S}
	GYP_DEFINES="${GYP_DEFINES}" export GYP_DEFINES
	# replace LD with CXX, to workaround a possible gyp issue?
	LD="${CXX}" export LD
	CC="${CC}" export CC
	CXX="${CXX}" export CXX
	CC_host="gcc" export CC_host
	CXX_host="g++" export CXX_host
	build/gyp_chromium --depth=. ${EXTRA_OEGYP}
}

do_compile() {
	# build with ninja
	ninja -C ${S}/out/Release chrome chrome_sandbox
}

do_install() {
	if [ -f "${WORKDIR}/google-chrome" ]; then
		install -Dm 0755 ${WORKDIR}/google-chrome ${D}${bindir}/google-chrome
		# Add extra command line arguments to google-chrome script by modifying
		# the dummy "CHROME_EXTRA_ARGS" line
		sed -i "s/^CHROME_EXTRA_ARGS=\"\"/CHROME_EXTRA_ARGS=\"${CHROMIUM_EXTRA_ARGS}\"/" ${D}${bindir}/google-chrome		
	fi
	if [ -f "${WORKDIR}/cef-simple" ]; then
		install -Dm 0755 ${WORKDIR}/cef-simple ${D}${bindir}/cef-simple
	fi
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/chrome_sandbox" ]; then
		install -Dm 4755 ${B}/out/${CHROMIUM_BUILD_TYPE}/chrome_sandbox ${D}${sbindir}/chrome-devel-sandbox
	fi
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/cefsimple" ]; then
		install -Dm 0755 ${B}/out/${CHROMIUM_BUILD_TYPE}/cefsimple ${D}${bindir}/${BPN}/cefsimple
	fi
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/lib/libcef.so" ]; then
		install -Dm 0755 ${B}/out/${CHROMIUM_BUILD_TYPE}/lib/libcef.so ${D}${libdir}/${BPN}/libcef.so
	fi
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/chrome" ]; then
		strip ${B}/out/${CHROMIUM_BUILD_TYPE}/chrome
		#paxctl -c -m ${B}/out/${CHROMIUM_BUILD_TYPE}/chrome
		install -Dm 0755 ${B}/out/${CHROMIUM_BUILD_TYPE}/chrome ${D}${bindir}/${BPN}/chrome
	fi
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/icudtl.dat" ]; then
		install -Dm 0644 ${B}/out/${CHROMIUM_BUILD_TYPE}/icudtl.dat ${D}${bindir}/${BPN}/icudtl.dat
	fi
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/natives_blob.bin" ]; then
		install -Dm 0644 ${B}/out/${CHROMIUM_BUILD_TYPE}/natives_blob.bin ${D}${bindir}/${BPN}/natives_blob.bin
	fi
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/snapshot_blob.bin" ]; then
		install -Dm 0644 ${B}/out/${CHROMIUM_BUILD_TYPE}/snapshot_blob.bin ${D}${bindir}/${BPN}/snapshot_blob.bin
	fi

	if [ -f "${WORKDIR}/google-chrome.desktop" ]; then
		install -Dm 0644 ${WORKDIR}/google-chrome.desktop ${D}${datadir}/applications/google-chrome.desktop
	fi
	#Chromium plugins libs
	for f in libpdf.so libosmesa.so libffmpegsumo.so; do
		if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/$f" ]; then
			install -Dm 0644 ${B}/out/${CHROMIUM_BUILD_TYPE}/$f ${D}${libdir}/${BPN}/$f
		fi
	done

	#Chromium *.pak files and CEF pak files ( prefixed with cef_
	for f in content_resources.pak keyboard_resources.pak chrome_100_percent.pak product_logo_48.png resources.pak \
	         cef_100_percent.pak cef_200_percent.pak cef_resources.pak cef.pak \
		 locales/en-US.pak; do
		if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/$f" ]; then
			install -Dm 0644 ${B}/out/${CHROMIUM_BUILD_TYPE}/$f ${D}${bindir}/${BPN}/$f
		fi
	done

	
	# Always adding this libdir (not just with component builds), because the
        # LD_LIBRARY_PATH line in the google-chromium script refers to it
        install -d ${D}${libdir}/${BPN}/
        if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'component-build', 'component-build', '', d)}" ]; then
                install -m 0755 ${B}/out/${CHROMIUM_BUILD_TYPE}/lib/*.so ${D}${libdir}/${BPN}/
        fi

}

INSANE_SKIP_${PN} += "already-stripped installed-vs-shipped"

PACKAGES =+ "${PN}-codecs-ffmpeg ${PN}-plugin-pdf"

FILES_${PN}-codecs-ffmpeg = "${libdir}/${BPN}/libffmpegsumo.so"
FILES_${PN}-plugin-pdf = "${libdir}/${BPN}/libpdf.so"

FILES_${PN} = "${bindir}/${BPN} ${datadir}/applications ${sbindir}/ ${libdir}/${BPN}/"
FILES_${PN} += "${bindir} ${libdir}"
FILES_${PN} += "${bindir}/${BPN}/*.pak"
FILES_${PN} += "${bindir}/${BPN}/locales/*.pak"

FILES_${PN}-dbg += "${bindir}/${BPN}/.debug/ ${libdir}/${BPN}/.debug/ ${libdir}/.debug/"

PACKAGE_DEBUG_SPLIT_STYLE = "debug-without-src"

INSANE_SKIP_${PN} = "ldflags"
SOLIBS = ".so"
FILES_SOLIBSDEV = ""
