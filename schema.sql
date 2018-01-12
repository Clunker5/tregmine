-- MySQL dump 10.13  Distrib 5.5.34, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: tregmine
-- ------------------------------------------------------
-- Server version	5.5.34-0ubuntu0.13.10.1


--
-- Table structure for table blessedblock
--

DROP FUNCTION IF EXISTS UNIX_TIMESTAMP();

CREATE FUNCTION UNIX_TIMESTAMP()
RETURNS INTEGER AS $$
DECLARE currentTime INTEGER;
BEGIN
        SELECT  (extract(epoch from now())) INTO currentTime;

        RETURN currentTime;
END;
$$  LANGUAGE plpgsql;

DROP TABLE IF EXISTS blessedblock;
CREATE TABLE blessedblock (
  blessedblock_id serial PRIMARY KEY,
  player_id int DEFAULT NULL,
  blessedblock_checksum int DEFAULT NULL,
  blessedblock_x int DEFAULT NULL,
  blessedblock_y int DEFAULT NULL,
  blessedblock_z int DEFAULT NULL,
  blessedblock_world text DEFAULT NULL
);

--
-- Table structure for table donation
--

DROP TABLE IF EXISTS donation;
CREATE TABLE donation (
  donation_id serial PRIMARY KEY,
  player_id int DEFAULT NULL,
  donation_timestamp int DEFAULT NULL,
  donation_amount text DEFAULT NULL,
  donation_paypalid text DEFAULT NULL,
  donation_payerid text DEFAULT NULL,
  donation_email text DEFAULT NULL,
  donation_firstname text DEFAULT NULL,
  donation_lastname text DEFAULT NULL,
  donation_message text
);

--
-- Table structure for table enchantment
--

DROP TABLE IF EXISTS enchantment;
CREATE TABLE enchantment (
  enchantment_name text PRIMARY KEY NOT NULL,
  enchantment_title text NOT NULL
);

--
-- Table structure for table fishyblock
--

DROP TABLE IF EXISTS fishyblock;

DROP TYPE IF EXISTS fishyblock_status CASCADE;
CREATE TYPE fishyblock_status AS ENUM ('active','deleted');

CREATE TABLE fishyblock (
  fishyblock_id serial PRIMARY KEY NOT NULL,
  player_id int DEFAULT NULL,
  fishyblock_created int DEFAULT NULL,
  fishyblock_status text DEFAULT 'active',
  fishyblock_material int DEFAULT NULL,
  fishyblock_data int DEFAULT NULL,
  fishyblock_enchantments text,
  fishyblock_cost int DEFAULT NULL,
  fishyblock_inventory int DEFAULT NULL,
  fishyblock_world text DEFAULT NULL,
  fishyblock_blockx int DEFAULT NULL,
  fishyblock_blocky int DEFAULT NULL,
  fishyblock_blockz int DEFAULT NULL,
  fishyblock_signx int DEFAULT NULL,
  fishyblock_signy int DEFAULT NULL,
  fishyblock_signz int DEFAULT NULL,
  fishyblock_storedenchants text DEFAULT '0'
);

--
-- Table structure for table fishyblock_costlog
--

DROP TABLE IF EXISTS fishyblock_costlog;
CREATE TABLE fishyblock_costlog (
  costlog_id serial PRIMARY KEY NOT NULL,
  fishyblock_id int DEFAULT NULL,
  costlog_timestamp int DEFAULT NULL,
  costlog_newcost int DEFAULT NULL,
  costlog_oldcost int DEFAULT NULL
);

--
-- Table structure for table fishyblock_transaction
--

DROP TABLE IF EXISTS fishyblock_transaction;

DROP TYPE IF EXISTS transaction_type CASCADE;
CREATE TYPE transaction_type AS ENUM ('deposit','withdraw','buy');

CREATE TABLE fishyblock_transaction (
  transaction_id serial PRIMARY KEY NOT NULL,
  fishyblock_id int DEFAULT NULL,
  player_id int DEFAULT NULL,
  transaction_type text DEFAULT NULL,
  transaction_timestamp int DEFAULT NULL,
  transaction_amount int DEFAULT NULL,
  transaction_unitcost int DEFAULT NULL,
  transaction_totalcost int DEFAULT NULL
);

--
-- Table structure for table inventory
--

DROP TABLE IF EXISTS inventory;

DROP TYPE IF EXISTS inventory_type CASCADE;
CREATE TYPE inventory_type AS ENUM ('block','player','player_armor');

CREATE TABLE inventory (
  inventory_id serial PRIMARY KEY NOT NULL,
  player_id int DEFAULT NULL,
  inventory_checksum int DEFAULT NULL,
  inventory_x int DEFAULT NULL,
  inventory_y int DEFAULT NULL,
  inventory_z int DEFAULT NULL,
  inventory_world text,
  inventory_player text,
  inventory_type text
);

--
-- Table structure for table inventory_accesslog
--

DROP TABLE IF EXISTS inventory_accesslog;
CREATE TABLE inventory_accesslog (
  accesslog_id serial PRIMARY KEY NOT NULL,
  inventory_id int DEFAULT NULL,
  player_id int DEFAULT NULL,
  accesslog_timestamp int DEFAULT NULL
);

--
-- Table structure for table inventory_changelog
--

DROP TABLE IF EXISTS inventory_changelog;

DROP TYPE IF EXISTS changelog_type CASCADE;
CREATE TYPE changelog_type AS ENUM ('add','remove');

CREATE TABLE inventory_changelog (
  changelog_id serial PRIMARY KEY NOT NULL,
  inventory_id int DEFAULT NULL,
  player_id int DEFAULT NULL,
  changelog_timestamp int DEFAULT NULL,
  changelog_slot int DEFAULT NULL,
  changelog_material int DEFAULT NULL,
  changelog_data int DEFAULT NULL,
  changelog_meta text,
  changelog_amount int DEFAULT NULL,
  changelog_type text DEFAULT NULL
);

--
-- Table structure for table inventory_item
--

DROP TABLE IF EXISTS inventory_item;
CREATE TABLE inventory_item (
  inventory_id int NOT NULL,
  item_slot int NOT NULL,
  item_material text DEFAULT 'AIR',
  item_data int DEFAULT 0,
  item_meta text,
  item_count int DEFAULT 0
);

--
-- Table structure for table item
--

DROP TABLE IF EXISTS item;
CREATE TABLE item (
  item_id int UNIQUE DEFAULT NULL,
  item_name text,
  item_value int DEFAULT NULL
);

--
-- Table structure for table mentorlog
--

DROP TABLE IF EXISTS mentorlog;

DROP TYPE IF EXISTS mentorlog_status CASCADE;
CREATE TYPE mentorlog_status AS ENUM ('started','completed','cancelled');

CREATE TABLE mentorlog (
  mentorlog_id serial NOT NULL,
  student_id int DEFAULT NULL,
  mentor_id int DEFAULT NULL,
  mentorlog_resumed int DEFAULT '0',
  mentorlog_startedtime int DEFAULT NULL,
  mentorlog_completedtime int DEFAULT '0',
  mentorlog_cancelledtime int DEFAULT '0',
  mentorlog_status text DEFAULT 'started',
  mentorlog_channel text DEFAULT NULL,
  PRIMARY KEY (mentorlog_id),
  UNIQUE (student_id,mentor_id),
  UNIQUE (mentor_id,student_id)
);

--
-- Table structure for table motd
--

DROP TABLE IF EXISTS motd;
CREATE TABLE motd (
  motd_id serial NOT NULL,
  motd_timestamp int NOT NULL,
  motd_message text,
  PRIMARY KEY (motd_id)
);

--
-- Table structure for table player
--

DROP TABLE IF EXISTS player;

DROP TYPE IF EXISTS player_confirmed CASCADE;
CREATE TYPE player_confirmed AS ENUM ('0','1');

CREATE TABLE player (
  player_id serial NOT NULL,
  player_name text,
  player_password text,
  player_email text,
  player_uuid text UNIQUE,
  player_confirmed text DEFAULT '0',
  player_created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  player_wallet bigint DEFAULT '50000',
  player_rank text DEFAULT 'unverified',
  player_flags int DEFAULT NULL,
  player_keywords text NOT NULL,
  player_ignore text DEFAULT NULL,
  player_inventory text DEFAULT NULL,
  PRIMARY KEY (player_id)
);

--
-- Table structure for table playerinventory
--

DROP TABLE IF EXISTS playerinventory;

CREATE TABLE playerinventory (
  playerinventory_id serial NOT NULL,
  player_id int NOT NULL,
  playerinventory_name text DEFAULT NULL,
  playerinventory_type text NOT NULL
);

-- --------------------------------------------------------

--
-- Table structure for table playerinventory_item
--

DROP TABLE IF EXISTS playerinventory_item;

CREATE TABLE playerinventory_item (
  playerinventory_id int DEFAULT NULL,
  item_slot int DEFAULT NULL,
  item_material varchar DEFAULT NULL,
  item_data int DEFAULT NULL,
  item_meta text,
  item_count int DEFAULT NULL,
  item_durability int DEFAULT NULL
);

--
-- Table structure for table player_badge
--

DROP TABLE IF EXISTS player_badge;
CREATE TABLE player_badge (
  badge_id serial NOT NULL,
  player_id int NOT NULL,
  badge_name text NOT NULL,
  badge_level int NOT NULL DEFAULT '0',
  badge_timestamp int NOT NULL,
  PRIMARY KEY (badge_id),
  UNIQUE (player_id,badge_name)
);

--
-- Table structure for table player_chatlog
--

DROP TABLE IF EXISTS player_chatlog;
CREATE TABLE player_chatlog (
  chatlog_id serial NOT NULL,
  player_id int DEFAULT NULL,
  chatlog_timestamp int DEFAULT NULL,
  chatlog_channel text DEFAULT NULL,
  chatlog_message text DEFAULT NULL,
  PRIMARY KEY (chatlog_id)
);

--
-- Table structure for table player_givelog
--

DROP TABLE IF EXISTS player_givelog;
CREATE TABLE player_givelog (
  givelog_id serial NOT NULL,
  sender_id int DEFAULT NULL,
  recipient_id int DEFAULT NULL,
  givelog_material int DEFAULT NULL,
  givelog_data int DEFAULT NULL,
  givelog_meta text,
  givelog_count int DEFAULT NULL,
  givelog_timestamp int DEFAULT NULL,
  PRIMARY KEY (givelog_id)
);

--
-- Table structure for table player_home
--

DROP TABLE IF EXISTS player_home;
CREATE TABLE player_home (
  home_id serial NOT NULL,
  player_id int DEFAULT NULL,
  home_name text,
  home_x float DEFAULT NULL,
  home_y float DEFAULT NULL,
  home_z float DEFAULT NULL,
  home_pitch float DEFAULT NULL,
  home_yaw float DEFAULT NULL,
  home_world text,
  home_time float DEFAULT NULL,
  PRIMARY KEY (home_id)
);

--
-- Table structure for table player_login
--

DROP TABLE IF EXISTS player_login;

DROP TYPE IF EXISTS login_action CASCADE;
CREATE TYPE login_action AS ENUM ('login','logout');

CREATE TABLE player_login (
  login_id serial NOT NULL,
  player_id int DEFAULT NULL,
  login_timestamp int DEFAULT NULL,
  login_action text DEFAULT NULL,
  login_country text,
  login_city text,
  login_ip text,
  login_hostname text,
  login_onlineplayers int DEFAULT NULL,
  PRIMARY KEY (login_id)
);

--
-- Table structure for table player_orelog
--

DROP TABLE IF EXISTS player_orelog;
CREATE TABLE player_orelog (
  orelog_id serial NOT NULL,
  player_id int DEFAULT NULL,
  orelog_material int DEFAULT NULL,
  orelog_timestamp int DEFAULT NULL,
  orelog_x int DEFAULT NULL,
  orelog_y int DEFAULT NULL,
  orelog_z int DEFAULT NULL,
  orelog_world text DEFAULT NULL,
  PRIMARY KEY (orelog_id)
);

--
-- Table structure for table player_property
--

DROP TABLE IF EXISTS player_property;
CREATE TABLE player_property (
  player_id int NOT NULL DEFAULT '0',
  property_key text NOT NULL DEFAULT '',
  property_value text,
  property_created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (player_id,property_key)
);

--
-- Table structure for table player_report
--

DROP TABLE IF EXISTS player_report;

DROP TYPE IF EXISTS report_action CASCADE;
CREATE TYPE report_action AS ENUM ('kick','softwarn','hardwarn','ban','comment');

CREATE TABLE player_report (
  report_id serial NOT NULL,
  subject_id int NOT NULL,
  issuer_id int NOT NULL,
  report_action text NOT NULL,
  report_message text NOT NULL,
  report_timestamp time NOT NULL DEFAULT NOW(),
  report_validuntil int DEFAULT NULL,
  PRIMARY KEY (report_id)
);

--
-- Table structure for table player_transaction
--

DROP TABLE IF EXISTS player_transaction;
CREATE TABLE player_transaction (
  transaction_id serial NOT NULL,
  sender_id int DEFAULT NULL,
  recipient_id int DEFAULT NULL,
  transaction_timestamp int DEFAULT NULL,
  transaction_amount int DEFAULT NULL,
  PRIMARY KEY (transaction_id)
);

--
-- Table structure for table player_webcookie
--

DROP TABLE IF EXISTS player_webcookie;
CREATE TABLE player_webcookie (
  webcookie_id serial NOT NULL,
  player_id int DEFAULT NULL,
  webcookie_nonce text DEFAULT NULL,
  PRIMARY KEY (webcookie_id),
  UNIQUE (webcookie_nonce)
);

--
-- Table structure for table shorturl
--

DROP TABLE IF EXISTS shorturl;
CREATE TABLE shorturl (
  urlID serial NOT NULL,
  link text NOT NULL,
  PRIMARY KEY (urlID)
);

--
-- Table structure for table stats_blocks
--

DROP TABLE IF EXISTS stats_blocks;
CREATE TABLE stats_blocks (
  checksum float NOT NULL,
  player text NOT NULL,
  x int NOT NULL,
  y int NOT NULL,
  z int NOT NULL,
  time float NOT NULL,
  status smallint NOT NULL,
  blockid float NOT NULL,
  world text NOT NULL DEFAULT 'world'
);

--
-- Table structure for table trade
--

DROP TABLE IF EXISTS trade;
CREATE TABLE trade (
  trade_id serial NOT NULL,
  sender_id int DEFAULT NULL,
  recipient_id int DEFAULT NULL,
  trade_timestamp int DEFAULT NULL,
  trade_amount int DEFAULT NULL,
  PRIMARY KEY (trade_id)
);

--
-- Table structure for table trade_item
--

DROP TABLE IF EXISTS trade_item;
CREATE TABLE trade_item (
  item_id serial NOT NULL,
  trade_id int DEFAULT NULL,
  item_material text DEFAULT NULL,
  item_data int DEFAULT NULL,
  item_meta text,
  item_count int DEFAULT NULL,
  PRIMARY KEY (item_id)
);

--
-- Table structure for table version
--

DROP TABLE IF EXISTS version;
CREATE TABLE version (
  version_id serial NOT NULL,
  version_number text NOT NULL,
  version_string text,
  PRIMARY KEY (version_id)
);

--
-- Table structure for table warp
--

DROP TABLE IF EXISTS warp;
CREATE TABLE warp (
  warp_id serial NOT NULL,
  warp_name text,
  warp_x float DEFAULT NULL,
  warp_y float DEFAULT NULL,
  warp_z float DEFAULT NULL,
  warp_pitch float DEFAULT NULL,
  warp_yaw float DEFAULT NULL,
  warp_world text,
  PRIMARY KEY (warp_id),
  UNIQUE (warp_name)
);

--
-- Table structure for table warp_log
--

DROP TABLE IF EXISTS warp_log;
CREATE TABLE warp_log (
  log_id serial NOT NULL,
  player_id int DEFAULT NULL,
  warp_id int DEFAULT NULL,
  log_timestamp int DEFAULT NULL,
  PRIMARY KEY (log_id)
);

--
-- Table structure for table zone
--

DROP TABLE IF EXISTS zone;
CREATE TABLE zone (
  zone_id serial NOT NULL,
  zone_world text NOT NULL DEFAULT 'world',
  zone_name text UNIQUE NOT NULL,
  zone_enterdefault text NOT NULL DEFAULT '1',
  zone_placedefault text NOT NULL DEFAULT '1',
  zone_destroydefault text NOT NULL DEFAULT '1',
  zone_pvp text NOT NULL DEFAULT '0',
  zone_hostiles text DEFAULT '1',
  zone_communist text DEFAULT '0',
  zone_publicprofile text DEFAULT '0',
  zone_entermessage text NOT NULL,
  zone_exitmessage text NOT NULL,
  zone_texture text,
  zone_owner text,
  zone_flags int DEFAULT '0',
  PRIMARY KEY (zone_id)
);

--
-- Table structure for table zone_lot
--

DROP TABLE IF EXISTS zone_lot;
CREATE TABLE zone_lot (
  lot_id serial NOT NULL,
  zone_id int NOT NULL,
  lot_name text NOT NULL,
  lot_x1 int NOT NULL,
  lot_y1 int NOT NULL,
  lot_x2 int NOT NULL,
  lot_y2 int NOT NULL,
  special int DEFAULT NULL,
  lot_flags int NOT NULL DEFAULT '3',
  PRIMARY KEY (lot_id)
);

--
-- Table structure for table zone_lotuser
--

DROP TABLE IF EXISTS zone_lotuser;
CREATE TABLE zone_lotuser (
  lot_id int NOT NULL DEFAULT '0',
  user_id int NOT NULL DEFAULT '0',
  PRIMARY KEY (lot_id,user_id)
);

--
-- Table structure for table zone_profile
--

DROP TABLE IF EXISTS zone_profile;
CREATE TABLE zone_profile (
  profile_id serial NOT NULL,
  zone_id int DEFAULT NULL,
  player_id int DEFAULT NULL,
  profile_timestamp int DEFAULT NULL,
  profile_text text,
  PRIMARY KEY (profile_id)
);

--
-- Table structure for table zone_rect
--

DROP TABLE IF EXISTS zone_rect;
CREATE TABLE zone_rect (
  rect_id serial NOT NULL,
  zone_id int DEFAULT NULL,
  rect_x1 int DEFAULT NULL,
  rect_y1 int DEFAULT NULL,
  rect_x2 int DEFAULT NULL,
  rect_y2 int DEFAULT NULL,
  PRIMARY KEY (rect_id)
);

--
-- Table structure for table zone_user
--

DROP TABLE IF EXISTS zone_user;

DROP TYPE IF EXISTS user_perm CASCADE;
CREATE TYPE user_perm AS ENUM ('owner','maker','allowed','banned');

CREATE TABLE zone_user (
  zone_id int NOT NULL DEFAULT '0',
  user_id int NOT NULL DEFAULT '0',
  user_perm text NOT NULL DEFAULT 'allowed',
  PRIMARY KEY (zone_id,user_id)
);

--
-- Table structure for table misc_message
--

DROP TABLE IF EXISTS misc_message;

CREATE TABLE misc_message (
  message_type text NOT NULL,
  message_value text NOT NULL
);


-- Dump completed on 2013-12-10 22:18:14
