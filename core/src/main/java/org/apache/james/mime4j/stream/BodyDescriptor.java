/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.james.mime4j.stream;

/**
 * A descriptor containing common MIME content body properties.
 */
public interface BodyDescriptor extends ContentDescriptor {

    /**
     * Returns the body descriptors boundary.
     * @return Boundary string, if known, or null. The latter may be the
     *   case, in particular, if the body is no multipart entity.
     */
    String getBoundary();

    /**
     * Returns the start byte position of the header for this body.
     *
     * @return Start byte position of the header.
     */
    int getHeaderStartByte();

    /**
     * Returns the start byte position of the body for this body.
     *
     * @return Start byte position of the body.
     */
    int getBodyStartByte();

    int getBodyEndByte();

}
