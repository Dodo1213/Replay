package net.giantgames.replay.session;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class SessionProfile implements Serializable {

    private final String gameId;
    private final String gameMode;
    private final long date;
}
