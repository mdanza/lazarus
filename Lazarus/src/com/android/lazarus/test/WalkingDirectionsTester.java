package com.android.lazarus.test;

import java.util.ArrayList;

import com.android.lazarus.VoiceInterpreterActivity;

public class WalkingDirectionsTester {

	private String[] instructions = { "abuelo", "si", "dos" };
	private int i = 0;
	private VoiceInterpreterActivity voiceInterpreterActivity;

	public WalkingDirectionsTester(
			VoiceInterpreterActivity voiceInterpreterActivity) {
		super();
		this.voiceInterpreterActivity = voiceInterpreterActivity;
	}

	public void setResultsOnState() {
		if (i < instructions.length) {
			ArrayList<String> results = new ArrayList<String>();
			results.add(instructions[i]);
			results.add(instructions[i]);
			results.add(instructions[i]);
			i++;
			voiceInterpreterActivity.getState().setResults(results);
		}
	}

}
