ifeq ($(IDP_ROOT),)
$(error IDP_ROOT is undefined)
endif

CURRENT_DIR = $(shell pwd)

ifneq ("$(wildcard $(IDP_ROOT)/wrlinux-7/wrlinux/configure)","")

CONFIGURE_CMD=$(IDP_ROOT)/wrlinux-7/wrlinux/configure
CONFIGURE_OPTIONS= --enable-board=intel-baytrail-64 --enable-kernel=idp --enable-prelink=no \
		   --enable-rootfs=idp --enable-addons=wr-idp --enable-jobs=6 --enable-parallel-pkgbuilds=6 \
		   --enable-reconfig --enable-rm-oldimgs=yes --enable-internet-download=yes --enable-build=production \
		   --without-layer=wr-mcafee,wr-srm 
LAYERS:=$(CURRENT_DIR)/meta-mi-3.0
WORK_DIR=$(CURRENT_DIR)/build-mi-3.0

else ifneq ("$(wildcard $(IDP_ROOT)/wrlinux-5/wrlinux/configure)","")

CONFIGURE_CMD=$(IDP_ROOT)/wrlinux-5/wrlinux/configure
CONFIGURE_OPTIONS= --enable-board=intel-atom-baytrail --enable-kernel=standard --enable-rootfs=glibc-idp \
                   --enable-addons=wr-idp --enable-jobs=6 --enable-parallel-pkgbuilds=6 \
                   --enable-reconfig --enable-internet-download=yes --with-rcpl-version=0024 --enable-build=production \
		   --with-template=feature/intel-emgd-baytrail \
                   --without-layer=meta-java,meta-java-dl,wr-mcafee,wr-srm 
LAYERS:=$(CURRENT_DIR)/meta-mi-2.0
WORK_DIR=$(CURRENT_DIR)/build-mi-2.0

else
$(error IDP_ROOT doesn't contain a valid idp sdk)
endif

.config:
	@mkdir -p $(WORK_DIR)
	@cd $(WORK_DIR) && \
	$(CONFIGURE_CMD) $(CONFIGURE_OPTIONS) --with-layer=$(LAYERS) \
		 
	@touch $@

config: .config

chromium: 
	@make config LAYERS=$(LAYERS),$(CURRENT_DIR)/meta-browser
	@make -C $(WORK_DIR) fs

burn-usb:
ifndef USB_DEV
	@echo "USB_DEV is not defined"
	@exit
endif
	@cd $(WORK_DIR) && \
	sudo ./deploy.sh -f export/images/wrlinux-image-idp-intel-baytrail-64.tar.bz2 -y -u -p 4G -d $(USB_DEV)

all: chromium

clean:
	@rm -f .config

distclean: clean
	@rm -rf $(WORK_DIR)
	
