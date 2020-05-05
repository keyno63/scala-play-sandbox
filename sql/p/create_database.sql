---
--- master table, monster.
---
CREATE TABLE IF NOT EXISTS m_monster
(
    id varchar(255) PRIMARY KEY,
    name varchar(255) NOT NULL,
    type1 varchar(255) NOT NULL,
    type2 varchar(255) DEFAULT NULL,
    chara1 varchar(255) NOT NULL,
    chara2 varchar(255) DEFAULT NULL,
    chara_hidden varchar(255) DEFAULT NULL,
    hp INT,
    attack INT,
    deffence INT,
    special_attack INT,
    special_deffence INT,
    speed INT,
    overall INT,
    create_time timestamp with time zone DEFAULT CURRENT_DATE,
    update_time timestamp with time zone DEFAULT CURRENT_DATE
);