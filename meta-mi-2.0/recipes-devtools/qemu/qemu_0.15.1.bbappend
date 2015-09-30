# Do not use NSS even if it's available as chromium dependency
EXTRA_OECONF += "--disable-smartcard-nss"
