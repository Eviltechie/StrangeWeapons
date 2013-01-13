CREATE TABLE IF NOT EXISTS `dropdata` (
  `username` varchar(16) NOT NULL,
  `playtime` int(6) NOT NULL,
  `nextitemdrop` int(6) NOT NULL,
  `nextcratedrop` int(6) NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;