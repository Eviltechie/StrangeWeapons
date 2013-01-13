CREATE TABLE IF NOT EXISTS `parts` (
  `weaponid` int(11) NOT NULL,
  `part` varchar(40) NOT NULL,
  `stat` int(11) NOT NULL,
  `partorder` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;