package com.junbeom.conversion;

import java.io.IOException;

public abstract class ConversionException extends Exception {

    public ConversionException(String message) {
        super(message);
    }

    public abstract static class ParseErr extends ConversionException {
        public ParseErr(String message) {
            super(message);
        }

        public static final class Empty extends ParseErr {
            public Empty() {
                super("cannot parse integer from empty string");
            }
        }

        public static final class NonDecimal extends ParseErr {
            public NonDecimal() {
                super("non-decimal character found in string");
            }
        }

        public static final class Overflow extends ParseErr {
            public Overflow() {
                super("integer overflow");
            }
        }

        public static final class NegOverflow extends ParseErr {
            public NegOverflow() {
                super("negative integer overflow");
            }
        }

        public static final class UnsignedNotAllowed extends ParseErr {
            public UnsignedNotAllowed() {
                super("unsigned integer not allowed");
            }
        }

        public static final class InvalidPointIndex extends ParseErr {
            public InvalidPointIndex() {
                super("invalid point index");
            }
        }

        public static final class InvalidPointLocation extends ParseErr {
            public InvalidPointLocation() {
                super("invalid point location");
            }
        }

        public static final class InvalidLength extends ParseErr {
            public InvalidLength() {
                super("invalid length");
            }
        }

        public static final class DivideByZero extends ParseErr {
            public DivideByZero() {
                super("divide by zero");
            }
        }
    }

    public abstract static class ConfigErr extends ConversionException {
        public ConfigErr(String message) {
            super(message);
        }

        public static final class ZeroNormalizer extends ConfigErr {
            public ZeroNormalizer() {
                super("normalizer must not be zero");
            }
        }

        public static final class InvalidSize extends ConfigErr {
            public InvalidSize() {
                super("invalid size");
            }
        }
    }
}