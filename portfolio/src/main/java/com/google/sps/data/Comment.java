package com.google.sps.data;

/** Holds information about any given comment. */
public final class Comment {
  private final long commentId;
  private final String message;
  private final long timestamp;

  public Comment(long commentId, String message, long timestamp) {
    this.commentId = commentId;
    this.message = message;
    this.timestamp = timestamp;
  }
}