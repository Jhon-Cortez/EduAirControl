export function getEnvironmentStatus(statusKey, t) {
  switch (statusKey) {
    case "dashboard.statusNormal":
      return {
        color: "#22c55e",
        text: t("dashboard.statusNormal"),
        bg: "#22c55e20"
      };

    case "dashboard.statusWarning":
      return {
        color: "#f59e0b",
        text: t("dashboard.statusWarning"),
        bg: "#f59e0b20"
      };

    case "dashboard.statusAlert":
      return {
        color: "#ef4444",
        text: t("dashboard.statusAlert"),
        bg: "#ef444420"
      };

    default:
      return {
        color: "#94a3b8",
        text: t("common.unknown"),
        bg: "#94a3b820"
      };
  }
}