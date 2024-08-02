package com.glitch.floweryapi.domain.utils.phoneverification

class PhoneNotFoundException(): Throwable("Phone not found or code has expired.")