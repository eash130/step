// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<TimeRange> meetingTimes = new ArrayList();
    Collection<String> meetingAttendees = request.getAttendees();
    long meetingDuration = request.getDuration();
    
    // If there are no required attendees, then the meeting can be held at any time of the day.
    if (meetingAttendees.isEmpty()) {
      meetingTimes.add(TimeRange.WHOLE_DAY);
      return meetingTimes;
    }

    // If the meeting lasts longer than the day, then there are no meeting time slots available.
    if (meetingDuration > TimeRange.WHOLE_DAY.duration()) {
      return meetingTimes;
    }

    // Organize events by start time in increasing order.
    List<Event> eventList = new ArrayList<>(events);
    Collections.sort(eventList, new Comparator<Event>() {
      public int compare(Event event1, Event event2) {
        return Integer.compare(event1.getWhen().start(), event2.getWhen().start());
      }
    });

    // Remove all events that do not include the meeting attendees.
    eventList.removeIf(event ->
        !(new HashSet<>(event.getAttendees()).removeAll(request.getAttendees())));

    // Start with an open schedule and split it up based on events.
    meetingTimes.add(TimeRange.WHOLE_DAY);
    for (Event event: eventList) {
      meetingTimes = splitSchedule(meetingTimes, event);
    }

    // Remove all time slots that are not long enough to hold the meeting.
    meetingTimes.removeIf(time -> time.duration() < request.getDuration());
    return meetingTimes;
  }

  /** 
   * Splits up the schedule to accomodate a particular event.
   *
   * @param meetingTimes The current available meeting time slots.
   * @param event The event that you are trying to build meeting times around.
   *
   * @return The new available meeting time slots after accomodating the event.
   */
  private Collection<TimeRange> splitSchedule(Collection<TimeRange> meetingTimes, Event event) {
    int eventStart = event.getWhen().start();
    int eventEnd = event.getWhen().end();
    Collection<TimeRange> newMeetingTimes = new ArrayList();
    for (TimeRange time: meetingTimes) {
      int timeSlotStart = time.start();
      int timeSlotEnd = time.end();
      if (timeSlotStart < eventStart && timeSlotEnd > eventEnd) {
        // Handles the case of when an event is entirely within a time slot.
        //
        // Time Slot : |-------------|
        // Event     :       |--A--|
        // New Slot  : |-----|     |-|
        newMeetingTimes.add(TimeRange.fromStartEnd(timeSlotStart, eventStart, false));
        newMeetingTimes.add(TimeRange.fromStartEnd(eventEnd, timeSlotEnd, false));

      } else if (timeSlotStart < eventStart &&
                 (timeSlotEnd > eventStart || timeSlotEnd == eventEnd)) {
        // Handles the case of when an event starts within the time slot,
        // but ends after the time slots end time.
        //
        // Time Slot : |-------------|
        // Event     :        |----A----|
        // New Slot  : |------|
        newMeetingTimes.add(TimeRange.fromStartEnd(timeSlotStart, eventStart, false));

      } else if ((timeSlotStart < eventEnd || timeSlotStart == eventStart) &&
                 timeSlotEnd > eventEnd) {
        // Handles the case of when an event ends after the time slot starts,
        // but starts before the time slot ends.
        //
        // Time Slot :   |-------------|
        // Event     : |----A----|
        // New Slot  :           |-----|
        newMeetingTimes.add(TimeRange.fromStartEnd(eventEnd, timeSlotEnd, false));

      } else {
        // If none of the conditions are met, then there is no need to change the time slot.
        newMeetingTimes.add(time);
      }
    }
    return newMeetingTimes;
  }
}
