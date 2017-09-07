package com.slack.demoapp.ttt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Models a ttt game.
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode
public class Game {
	
	String channeld;
	
	String challengerId;
	
	String challengeeId;
	
	String nextPlayerId;
	
	String board;
}
