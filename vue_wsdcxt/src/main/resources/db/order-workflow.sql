CREATE TABLE IF NOT EXISTS order_events (
    eventid varchar(36) NOT NULL PRIMARY KEY,
    ordersid varchar(32) NOT NULL,
    action varchar(40) NOT NULL,
    from_status varchar(20) NOT NULL,
    to_status varchar(20) NOT NULL,
    actor_role varchar(10) NOT NULL,
    actor_id varchar(32) NOT NULL,
    reason varchar(500) NOT NULL,
    created_at timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX order_events_order (ordersid, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

