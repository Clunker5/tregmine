package info.tregmine.database;

import info.tregmine.api.TregminePlayer;

public interface IMentorLogDAO {
    int getMentorLogId(TregminePlayer student, TregminePlayer mentor) throws DAOException;

    void insertMentorLog(TregminePlayer student, TregminePlayer mentor) throws DAOException;

    void updateMentorLogChannel(int mentorLogId, String channel) throws DAOException;

    void updateMentorLogEvent(int mentorLogId, MentoringEvent event) throws DAOException;

    void updateMentorLogResume(int mentorLogId) throws DAOException;

    enum MentoringEvent {
        STARTED, COMPLETED, CANCELLED, SKIPPED;

        public MentoringEvent fromString(String str) {
            for (MentoringEvent event : MentoringEvent.values()) {
                if (event.toString().equalsIgnoreCase(str)) {
                    return event;
                }
            }

            return null;
        }
    }
}
