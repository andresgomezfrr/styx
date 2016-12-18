/*-
 * -\-\-
 * Spotify Styx CLI
 * --
 * Copyright (C) 2016 Spotify AB
 * --
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
 * -/-/-
 */

package com.spotify.styx.cli;

import static com.spotify.styx.cli.CliUtil.formatTimestamp;

import com.spotify.styx.api.cli.ActiveStatesPayload;
import com.spotify.styx.model.WorkflowId;
import com.spotify.styx.state.StateData;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 * Cli output printer that prints more unix tool friendly output
 */
class PlainCliOutput implements CliOutput {

  @Override
  public void printActiveStates(ActiveStatesPayload activeStatesPayload) {
    SortedMap<WorkflowId, SortedSet<ActiveStatesPayload.ActiveState>> groupedActiveStates =
        CliUtil.groupActiveStates(activeStatesPayload.activeStates());

    groupedActiveStates.entrySet().forEach(entry -> {
      WorkflowId workflowId = entry.getKey();
      entry.getValue().forEach(activeState -> {
        final StateData stateData = activeState.stateData();
        final List<StateData.Message> messages = stateData.messages();

        final String lastMessage;
        if (messages.isEmpty()) {
          lastMessage = "No info";
        } else {
          final StateData.Message message = messages.get(messages.size() - 1);
          lastMessage = message.line();
        }

        System.out.println(String.format(
            "%s %s %s %s %s %s",
            workflowId.componentId(),
            workflowId.endpointId(),
            activeState.workflowInstance().parameter(),
            activeState.state(),
            stateData.executionId().orElse("<no execution id>"),
            lastMessage));
      });
    });
  }

  @Override
  public void printEvents(List<EventInfo> eventInfos) {
    eventInfos.forEach(
        eventInfo ->
            System.out.println(String.format("%s %s %s",
                                             formatTimestamp(eventInfo.timestamp()),
                                             eventInfo.name(),
                                             eventInfo.info()))
    );
  }
}
