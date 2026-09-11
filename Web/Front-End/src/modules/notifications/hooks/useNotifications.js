import { useMemo, useState, useEffect } from "react";
import { useAllEnvironmentsVM } from "../../../viewmodels/useAllEnvironmentsVM";

export const useNotifications = () => {
  const { environments } = useAllEnvironmentsVM();

  // Generar notificaciones a partir de los ambientes
  const generatedNotifications = useMemo(() => {
    const list = [];

    let alerts = 0;
    let warnings = 0;

    environments.forEach((env) => {
      // CO₂
      if (env.co2 > 1000) {
        alerts++;

        list.push({
          id: `co2-${env.id}`,
          type: "danger",
          title: "notifications.co2High",
          message: "notifications.co2Message",
          data: {
            name: env.nameKey,
            value: env.co2,
          },
          time: new Date(),
          read: false,
        });
      }

      // Temperatura
      if (env.temp > 28) {
        alerts++;

        list.push({
          id: `temp-${env.id}`,
          type: "danger",
          title: "notifications.tempHigh",
          message: "notifications.tempMessage",
          data: {
            name: env.nameKey,
            value: env.temp,
          },
          time: new Date(),
          read: false,
        });
      }

      // Ruido
      if (env.noise > 70) {
        warnings++;

        list.push({
          id: `noise-${env.id}`,
          type: "warning",
          title: "notifications.warning",
          message: "notifications.warningMessage",
          data: {
            name: env.nameKey,
          },
          time: new Date(),
          read: false,
        });
      }
    });

    // Resumen diario
    list.push({
      id: "summary",
      type: "info",
      title: "notifications.dailySummary",
      message: "notifications.summaryMessage",
      data: {
        alerts,
        warnings,
      },
      time: new Date(),
      read: false,
    });

    // Más recientes primero
    list.sort((a, b) => b.time.getTime() - a.time.getTime());

    return list;
  }, [environments]);

  // Estado real de notificaciones
  const [notifications, setNotifications] = useState([]);

  /**
   * Mantiene el estado de "leídas"
   * cuando cambian los ambientes.
   */
  useEffect(() => {
    setNotifications((prev) => {
      return generatedNotifications.map((newNotification) => {
        const old = prev.find((n) => n.id === newNotification.id);

        if (old) {
          return {
            ...newNotification,
            read: old.read,
          };
        }

        return newNotification;
      });
    });
  }, [generatedNotifications]);

  // Cantidad sin leer
  const unreadCount = useMemo(() => {
    return notifications.filter((n) => !n.read).length;
  }, [notifications]);

  // Marcar una
  const markAsRead = (id) => {
    setNotifications((prev) =>
      prev.map((notification) =>
        notification.id === id
          ? {
              ...notification,
              read: true,
            }
          : notification
      )
    );
  };

  // Marcar todas
  const markAllRead = () => {
    setNotifications((prev) =>
      prev.map((notification) => ({
        ...notification,
        read: true,
      }))
    );
  };

  return {
    notifications,
    unreadCount,
    markAsRead,
    markAllRead,
  };
};