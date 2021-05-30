package fr.raksrinana.rsndiscord.command;

public enum CommandResult{
	/**
	 * Will send a predefined response.
	 */
	BAD_ARGUMENTS,
	/**
	 * Will send an error message.
	 */
	FAILED,
	/**
	 * Indicates the command was successful but no message were sent.
	 */
	SUCCESS,
	/**
	 * Indicates that no response has been sent.
	 */
	SUCCESS_NO_MESSAGE,
	/**
	 * Will send a predefined message.
	 */
	NOT_ALLOWED
}
