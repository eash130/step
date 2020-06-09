package com.google.sps.data;

/** Holds information about any given comment. */
public final class Comment {
  private final long commentId;
  private final String message;
  private final long timestamp;
  private final String email;

  public Comment(long commentId, String message, long timestamp, String email) {
    this.commentId = commentId;
    this.message = message;
    this.timestamp = timestamp;
    this.email = email;
  }
}
