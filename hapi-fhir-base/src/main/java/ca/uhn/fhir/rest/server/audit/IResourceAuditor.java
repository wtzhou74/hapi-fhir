package ca.uhn.fhir.rest.server.audit;

/*
 * #%L
 * HAPI FHIR - Core Library
 * %%
 * Copyright (C) 2014 University Health Network
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.List;

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.base.composite.BaseIdentifierDt;
import ca.uhn.fhir.model.dstu.valueset.SecurityEventObjectSensitivityEnum;
import ca.uhn.fhir.model.dstu.valueset.SecurityEventObjectTypeEnum;


public interface IResourceAuditor<T extends IResource> {
		
	public T getResource();
	public void setResource(T resource);
	
	public boolean isAuditable();
	
	public String getName();
	public BaseIdentifierDt getIdentifier();
	public SecurityEventObjectTypeEnum getType();	
	public String getDescription();
	//public List<ObjectDetail> getDetail(); //see operationoutcome -- need to move specifics insto dstu and keep base objects in core
	public SecurityEventObjectSensitivityEnum getSensitivity();

}