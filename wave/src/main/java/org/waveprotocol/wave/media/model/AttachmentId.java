/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.waveprotocol.wave.media.model;


import org.waveprotocol.wave.model.id.InvalidIdException;
import org.waveprotocol.wave.model.util.Preconditions;

/**
 * An attachment is identified by a tuple of a service provider id and an
 * identifying token unique to that provider.
 *
 */
public final class AttachmentId implements Comparable<AttachmentId> {

  /** Separates the domain from the id in an attachment id. */
  public static final String ATTACHMENT_PART_SEPARATOR = "/";

  private final String domain;
  private final String id;
  private volatile String serialized = null;

  /**
   * Creates an AttachmentId from a serialized attachment id.
   *
   * @param attachmentIdString a serialized attachment id
   * @return an AttachmentId
   * @throws InvalidIdException if the id cannot be parsed.
   */
  public static AttachmentId deserialise(String attachmentIdString)
      throws InvalidIdException {
    Preconditions.checkNotNull(attachmentIdString, "Null attachmentIdString");
    String[] parts = attachmentIdString.split(ATTACHMENT_PART_SEPARATOR);

    // Two part ids are the expected case (eg. domain/id).
    if (parts.length == 2) {
      return new AttachmentId(parts[0], parts[1]);
    }

    // One part ids are for legacy ids (pre-migration).
    // TODO(user): Remove support for legacy ids after the migration.
    if (parts.length == 1) {
      return new AttachmentId("", parts[0]);
    }

    // Not a valid id, throw an exception.
    throw new InvalidIdException(attachmentIdString,
        "Unable to deserialise the attachment id: " + attachmentIdString +
        ". The attachment id needs to look like <domain>/<id> or <id>");
  }

  /**
   * Constructs an attachment id from domain and id components. Neither the
   * domain nor the id may be null or contain the '/' character used to
   * separate components. An exception will be thrown if these requirements are
   * violated.
   *
   * @param domain must not be null.
   * @param id must not be null.
   */
  public AttachmentId(String domain, String id) {
    Preconditions.checkNotNull(domain, "Null domain");
    Preconditions.checkNotNull(id, "Null id");

    if (domain.contains(ATTACHMENT_PART_SEPARATOR)) {
      Preconditions.illegalArgument(
          "An AttachmentId domain component cannot contain the '/' domain separator: " + domain);
    }
    if (id.contains(ATTACHMENT_PART_SEPARATOR)) {
      Preconditions.illegalArgument(
          "An AttachmentId id component cannot contain the '/' id separator: " + id);
    }

    this.domain = domain.intern();
    this.id = id;
  }

  /**
   * @return the domain
   */
  public String getDomain() {
    return domain;
  }

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Serialises this attachmentId into a unique string. For any two attachment
   * ids, attachmentId1.serialise().equals(attachmentId2.serialise()) iff
   * attachmentId1.equals(attachmentId2).
   */
  public String serialise() {
    if (serialized == null) {

      // TODO(user): Remove support for domain-less legacy ids after the
      // migration.
      if (domain.isEmpty()) {
        serialized = id;
      } else {
        serialized = domain + ATTACHMENT_PART_SEPARATOR + id;
      }
    }
    return serialized;
  }

  /**
   * Generated by eclipse.
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + domain.hashCode();
    result = prime * result + id.hashCode();
    return result;
  }

  /**
   * Generated by eclipse.
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (getClass() != obj.getClass()) { return false; }
    AttachmentId other = (AttachmentId) obj;
    return domain == other.domain && id.equals(other.id);
  }

  @Override
  public String toString() {
    return "[AttachmentId:" + serialise() + "]";
  }

  @Override
  public int compareTo(AttachmentId other) {
    int domainCompare = domain.compareTo(other.domain);
    if (domainCompare == 0) {
      return id.compareTo(other.id);
    } else {
      return domainCompare;
    }
  }
}
