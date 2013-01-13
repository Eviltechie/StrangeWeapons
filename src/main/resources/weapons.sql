CREATE TABLE IF NOT EXISTS `weapons` (
  `weaponid` int(11) NOT NULL AUTO_INCREMENT,
  `quality` varchar(20) NOT NULL,
  `customname` varchar(40) DEFAULT NULL,
  `description` varchar(60) DEFAULT NULL,
  PRIMARY KEY (`weaponid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;