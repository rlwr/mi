ifeq ($(IDP_ROOT),)
$(error IDP_ROOT is undefined)
endif

CURRENT_DIR = $(shell pwd)

LAYERS:=$(CURRENT_DIR)/meta-intel,$(CURRENT_DIR)/meta-iot,$(CURRENT_DIR)/meta-browser

ifneq ("$(wildcard $(IDP_ROOT)/wrlinux-7/wrlinux/configure)","")

CONFIGURE_CMD=$(IDP_ROOT)/wrlinux-7/wrlinux/configure
CONFIGURE_OPTIONS= --enable-board=intel-baytrail-64 --enable-kernel=idp --enable-prelink=no \
		   --enable-rootfs=idp --enable-addons=wr-idp --enable-jobs=6 --enable-parallel-pkgbuilds=6 \
		   --enable-reconfig --enable-rm-oldimgs=yes --enable-internet-download=yes --enable-build=production \
		   --with-template=feature/executable-memory-protection \
		   --without-layer=wr-mcafee,wr-srm 
DEPLOY_OPTIONS= -f export/images/wrlinux-image-idp-intel-baytrail-64.tar.bz2 -y -u -p 4G
LAYERS:=$(LAYERS),$(CURRENT_DIR)/meta-mi-3.0,wr-iot
WORK_DIR=$(CURRENT_DIR)/build-mi-3.0

else ifneq ("$(wildcard $(IDP_ROOT)/wrlinux-5/wrlinux/configure)","")

CONFIGURE_CMD=$(IDP_ROOT)/wrlinux-5/wrlinux/configure
CONFIGURE_OPTIONS= --enable-board=intel-atom-baytrail --enable-kernel=standard --enable-rootfs=glibc-idp \
                   --enable-addons=wr-idp --enable-jobs=6 --enable-parallel-pkgbuilds=6 \
                   --enable-reconfig --enable-internet-download=yes --with-rcpl-version=0024 --enable-build=production \
		   --with-template=feature/intel-emgd-baytrail \
                   --without-layer=meta-java,meta-java-dl,wr-mcafee,wr-srm 
DEPLOY_OPTIONS= -f export/images/wrlinux-image-glibc-idp-intel-atom-baytrail.tar.bz2 -y -u -b intel-atom-baytrail -g ./grub-0.97
LAYERS:=$(LAYERS),$(CURRENT_DIR)/meta-mi-2.0
WORK_DIR=$(CURRENT_DIR)/build-mi-2.0

else
$(error IDP_ROOT doesn't contain a valid idp sdk)
endif

all: image

image: .config
	@make -C $(WORK_DIR) fs

.config:
	@mkdir -p $(WORK_DIR)
	@cd $(WORK_DIR) && \
	$(CONFIGURE_CMD) $(CONFIGURE_OPTIONS) --with-layer=$(LAYERS) \
		 
	@touch $@

config: .config


burn-usb:
ifndef USB_DEV
	@echo "USB_DEV is not defined"
	@exit
endif
	@cd $(WORK_DIR) && \
	sudo ./deploy.sh $(DEPLOY_OPTIONS) -d $(USB_DEV)
	echo " "


clean:
	@rm -f .config

distclean: clean
	@rm -rf $(WORK_DIR)
	
