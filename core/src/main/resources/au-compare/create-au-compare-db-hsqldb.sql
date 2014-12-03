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
CREATE TABLE auCompare!tableSuffix! (
  "id" INTEGER IDENTITY ,
  "docID" VARCHAR(255) NOT NULL ,
  "mapping" VARCHAR(255) NOT NULL ,
  "start" INTEGER NOT NULL ,
  "end" INTEGER NOT NULL ,
  "type" VARCHAR(255) NOT NULL ,
  "coveredText" VARCHAR(2000) NOT NULL ,
  "statClass" VARCHAR(64) NOT NULL
)