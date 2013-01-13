CREATE TABLE IF NOT EXISTS `droprecords` (
  `username` varchar(16) NOT NULL,
  `itemdropped` varchar(512) NOT NULL,
  `iscrate` tinyint(1) NOT NULL,
  `when` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=MyISAM DEFAULT CHARSET=latin1;