package utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

public class CloudinaryUtil {
    private static Cloudinary cloudinary;

    static {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dlecit4kf",
                "api_key", "918872146712184",
                "api_secret", "kWyR4EfEzxBfPMWMEijl600yNmI",
                "secure", true
        ));
    }

    public static Cloudinary getCloudinary() {
        return cloudinary;
    }
}
