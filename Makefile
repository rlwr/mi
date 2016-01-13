ifeq ($(IDP_ROOT),)
$(error IDP_ROOT is undefined)
endif

CURRENT_DIR = $(shell pwd)
SRM_ENABLED ?= "no"

LAYERS:=$(CURRENT_DIR)/meta-mi-3.0,$(CURRENT_DIR)/meta-intel,$(CURRENT_DIR)/meta-iot
# RL: Disable browser for now as the build fails on my setup
#LAYERS:=$(LAYERS),$(CURRENT_DIR)/meta-browser
# HDC layers
LAYERS:=$(LAYERS),wr-iot,sys-version,wr-hdc-examples

# Disable security for development scenarios
EXCLUDE_LAYERS=wr-mcafee,wr-ima-appraise

# Disable grsec altogether
FEATURES:=feature/nogrsec
# HDC features
FEATURES:=$(FEATURES),feature/package-management,feature/remote-session,feature/realtek,feature/online_updates,feature/recovery,feature/wra-demo-linux

EXCLUDE_LAYERS=wr-mcafee

IMAGE_NAME=wrlinux-image-idp-intel-baytrail-64-dist-srm.tar.bz2 
ifeq ($(SRM_ENABLED),"no")
EXCLUDE_LAYERS := $(EXCLUDE_LAYERS),wr-srm
IMAGE_NAME=wrlinux-image-idp-intel-baytrail-64.tar.bz2
endif

CONFIGURE_CMD=$(IDP_ROOT)/wrlinux-7/wrlinux/configure

CONFIGURE_OPTIONS= --enable-board=intel-baytrail-64 \
		   --enable-kernel=idp \
		   --enable-prelink=no \
		   --enable-rootfs=idp \
		   --enable-addons=wr-idp  \
		   --enable-reconfig \
		   --enable-rm-oldimgs=yes \
		   --enable-internet-download=yes \
		   --enable-build=production \
		   --without-layer=$(EXCLUDE_LAYERS) \
		   --with-layer=$(LAYERS) \
		   --with-template=$(FEATURES)

ifneq ($(SSTATE_DIR),)
CONFIGURE_OPTIONS += --with-sstate-dir=$(IDP_ROOT)
endif


DEPLOY_OPTIONS= -f export/images/$(IMAGE_NAME) -y -u -p 4G
WORK_DIR=$(CURRENT_DIR)/build-mi-3.0

#----------------------------------------------------------------------------------------------------------------
.fetch:
# Use experimental meta-iot layer
# TODO: need to merge with mainstream
	@if [ ! -d "$(CURRENT_DIR)/meta-iot" ]; then \
		git clone https://github.com/rlwr/meta-iot.git; \
	fi

	@if [ ! -d "$(CURRENT_DIR)/meta-browser" ]; then \
        	git clone https://github.com/emea-ssg-drd/meta-browser.git; \
    	fi

image: .config
	@make -C $(WORK_DIR) fs


.config: .fetch
	@echo "Configure Options: "$(CONFIGURE_OPTIONS)
	@mkdir -p $(WORK_DIR)
	@cd $(WORK_DIR) && \
	$(CONFIGURE_CMD) $(CONFIGURE_OPTIONS)  \
		 
	@touch $@

config: .config


burn-usb:
ifndef USB_DEV
	@echo "USB_DEV is not defined"
	@exit
endif
	@echo "Deploy options :"$(DEPLOY_OPTIONS)
	@cd $(WORK_DIR) && \
	sudo ./deploy.sh $(DEPLOY_OPTIONS) -d $(USB_DEV) 

clean:
	@rm -f .config

distclean: clean
	@rm -rf $(WORK_DIR)
	
