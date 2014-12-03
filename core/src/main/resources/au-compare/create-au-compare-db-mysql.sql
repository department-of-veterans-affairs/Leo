---
-- #%L
-- Leo Core
-- %%
-- Copyright (C) 2010 - 2014 Department of Veterans Affairs
-- %%
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
--      http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- #L%
---
DROP TABLE IF EXISTS `auCompare!tableSuffix!` ;

CREATE TABLE IF NOT EXISTS `auCompare!tableSuffix!`
  `id` SMALLINT(6) NOT NULL AUTO_INCREMENT ,
  'docId' VARCHAR(255) NOT NULL,
  'mapping' VARCHAR(255) NOT NULL,
  `start` INT(11) NOT NULL ,
  `end` INT(11) NOT NULL ,
  `type` VARCHAR(255) NOT NULL,
  `coveredText` VARCHAR(2000) NOT NULL ,
  `statClass` VARCHAR(64) NOT NULL )
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;