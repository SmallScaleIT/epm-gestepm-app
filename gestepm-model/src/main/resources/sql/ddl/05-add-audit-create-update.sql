
#No programmed share

ALTER TABLE `no_programmed_share` ADD COLUMN  `modified_by` int(10) unsigned DEFAULT NULL;

ALTER TABLE `no_programmed_share` ADD COLUMN  `modified_at` datetime DEFAULT NULL;

#Teleworking signing

ALTER TABLE `teleworking_signing` ADD COLUMN  `modified_by` int(10) unsigned DEFAULT NULL;

ALTER TABLE `teleworking_signing` ADD COLUMN  `modified_at` datetime DEFAULT NULL;

#Warehouse signing

ALTER TABLE `warehouse_signing` ADD COLUMN  `modified_by` int(10) unsigned DEFAULT NULL;

ALTER TABLE `warehouse_signing` ADD COLUMN  `modified_at` datetime DEFAULT NULL;

#Worshop signing

ALTER TABLE `workshop_signing` ADD COLUMN  `modified_by` int(10) unsigned DEFAULT NULL;

ALTER TABLE `workshop_signing` ADD COLUMN  `modified_at` datetime DEFAULT NULL;

#Personal signing

ALTER TABLE `personal_signing` ADD COLUMN  `modified_by` int(10) unsigned DEFAULT NULL;

ALTER TABLE `personal_signing` ADD COLUMN  `modified_at` datetime DEFAULT NULL;


#Inspection

ALTER TABLE `inspection` ADD COLUMN  `modified_by` int(10) unsigned DEFAULT NULL;

ALTER TABLE `inspection` ADD COLUMN  `modified_at` datetime DEFAULT NULL;


#Share break

ALTER TABLE `share_break` ADD COLUMN  `modified_by` int(10) unsigned DEFAULT NULL;

ALTER TABLE `share_break` ADD COLUMN  `modified_at` datetime DEFAULT NULL;


#Construction share

ALTER TABLE `construction_share` ADD COLUMN  `modified_by` int(10) unsigned DEFAULT NULL;

ALTER TABLE `construction_share` ADD COLUMN  `modified_at` datetime DEFAULT NULL;


#Programmed share

ALTER TABLE `programmed_share` ADD COLUMN  `modified_by` int(10) unsigned DEFAULT NULL;

ALTER TABLE `programmed_share` ADD COLUMN  `modified_at` datetime DEFAULT NULL;


#Work share

ALTER TABLE `work_share` ADD COLUMN  `modified_by` int(10) unsigned DEFAULT NULL;

ALTER TABLE `work_share` ADD COLUMN  `modified_at` datetime DEFAULT NULL;

