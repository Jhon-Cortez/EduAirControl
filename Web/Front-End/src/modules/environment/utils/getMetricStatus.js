export default function getMetricStatus(type, value, t) {
    switch (type) {
        case "temp":
            return value >= 20 && value <= 26
                ? {
                      text: t("allEnvironments.statusIdeal"),
                      color: "#2ecc71",
                  }
                : {
                      text: t("allEnvironments.statusHigh"),
                      color: "#f39c12",
                  };

        case "humidity":
            return value >= 40 && value <= 60
                ? {
                      text: t("allEnvironments.statusIdeal"),
                      color: "#2ecc71",
                  }
                : {
                      text: t("allEnvironments.statusOutOfRange"),
                      color: "#f39c12",
                  };

        case "co2":
            if (value <= 800)
                return {
                    text: t("allEnvironments.co2Good"),
                    color: "#2ecc71",
                };

            if (value <= 1000)
                return {
                    text: t("allEnvironments.co2Regular"),
                    color: "#f39c12",
                };

            return {
                text: t("allEnvironments.co2Bad"),
                color: "#e74c3c",
            };

        case "noise":
            if (value <= 50)
                return {
                    text: t("allEnvironments.noiseLow"),
                    color: "#2ecc71",
                };

            if (value <= 65)
                return {
                    text: t("allEnvironments.noiseModerate"),
                    color: "#f39c12",
                };

            return {
                text: t("allEnvironments.noiseHigh"),
                color: "#e74c3c",
            };

        default:
            return {
                text: "",
                color: "#999",
            };
    }
}