import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/*

Just make sure the public key (of the machine you run this script from) is in the authorized keys file of all of the remotes

Just have all class files in root (~), it's easier

To compile (on windows) (modify path for your own computer):
javac -classpath '.;c:\Users\r2che\Desktop\CNT5106C_BitTorrent\P2P File Share\lib\jsch-0.1.54.jar;' 'c:\Users\r2che\Desktop\CNT5106C_BitTorrent\P2P File Share\src\StartRemotePeers.java'

To run:
java -classpath ".:jsch-0.1.54.jar:" StartRemotePeers

To re-copy over (modify path for your own computer):
scp 'c:\Users\r2che\Desktop\CNT5106C_BitTorrent\P2P File Share\src\StartRemotePeers.class' 'c:\Users\r2che\Desktop\CNT5106C_BitTorrent\P2P File Share\src\StartRemotePeers$1.class' 'c:\Users\r2che\Desktop\CNT5106C_BitTorrent\P2P File Share\src\StartRemotePeers$PeerInfo.class' rangerchenore@lin114-01.cise.ufl.edu:~

======================================================================================================================================================================================================================================================

For Mac:

Change directory to src:
cd '/Users/rangerchenore/GithubProjects/CNT5106C_BitTorrent/P2P File Share/src'

Make sure JAVA_HOME points to Java 11:
export JAVA_HOME=`/usr/libexec/java_home -v 11.0.13`

Make source list:
find . -name "*.java" > sources.txt

Compile with .jar file:
javac -d '/Users/rangerchenore/GithubProjects/CNT5106C_BitTorrent/P2P File Share/bin' -cp '.:/Users/rangerchenore/GithubProjects/CNT5106C_BitTorrent/P2P File Share/bin/jsch-0.1.54.jar' @sources.txt

Copy the class files over with:
scp /Users/rangerchenore/GithubProjects/CNT5106C_BitTorrent/P2P\ File\ Share/bin/*.class rangerchenore@lin114-01.cise.ufl.edu:~

To copy config and starting image:
scp -r '/Users/rangerchenore/GithubProjects/CNT5106C_BitTorrent/P2P File Share/bin/config' rangerchenore@lin114-01.cise.ufl.edu:~ 
scp -r '/Users/rangerchenore/GithubProjects/CNT5106C_BitTorrent/P2P File Share/bin/FileToShare' rangerchenore@lin114-01.cise.ufl.edu:~


Altogether in one line:
cd '/Users/rangerchenore/GithubProjects/CNT5106C_BitTorrent/P2P File Share/src' && find . -name "*.java" > sources.txt && javac -d '/Users/rangerchenore/GithubProjects/CNT5106C_BitTorrent/P2P File Share/bin' -cp '.:/Users/rangerchenore/GithubProjects/CNT5106C_BitTorrent/P2P File Share/bin/jsch-0.1.54.jar' @sources.txt && scp /Users/rangerchenore/GithubProjects/CNT5106C_BitTorrent/P2P\ File\ Share/bin/*.class rangerchenore@lin114-01.cise.ufl.edu:~ && scp -r '/Users/rangerchenore/GithubProjects/CNT5106C_BitTorrent/P2P File Share/bin/config' rangerchenore@lin114-01.cise.ufl.edu:~  && scp -r '/Users/rangerchenore/GithubProjects/CNT5106C_BitTorrent/P2P File Share/bin/FileToShare' rangerchenore@lin114-01.cise.ufl.edu:~

Compile everything without sending over scp:
cd '/Users/rangerchenore/GithubProjects/CNT5106C_BitTorrent/P2P File Share/src' && export JAVA_HOME=`/usr/libexec/java_home -v 11.0.13` && find . -name "*.java" > sources.txt && javac -d '/Users/rangerchenore/GithubProjects/CNT5106C_BitTorrent/P2P File Share/bin' -cp '.:/Users/rangerchenore/GithubProjects/CNT5106C_BitTorrent/P2P File Share/bin/jsch-0.1.54.jar' @sources.txt

Run StartRemotePeers:
cd '/Users/rangerchenore/GithubProjects/CNT5106C_BitTorrent/P2P File Share/bin' && java -cp '.:/Users/rangerchenore/GithubProjects/CNT5106C_BitTorrent/P2P File Share/bin/jsch-0.1.54.jar' StartRemotePeers

Run PeerProcess locally:
cd '/Users/rangerchenore/GithubProjects/CNT5106C_BitTorrent/P2P File Share/bin' && java -cp '.:/Users/rangerchenore/GithubProjects/CNT5106C_BitTorrent/P2P File Share/bin/jsch-0.1.54.jar' PeerProcess 1001

*/

// TODO: Add better debugger log

public class StartRemotePeers {

    private static final String scriptPrefix = "java -cp \".:jsch-0.1.54.jar\" PeerProcess ";

    public static class PeerInfo {

        private String peerID;
        private String hostName;

        public PeerInfo(String peerID, String hostName) {
            super();
            this.peerID = peerID;
            this.hostName = hostName;
        }

        public String getPeerID() {
            return peerID;
        }

        public void setPeerID(String peerID) {
            this.peerID = peerID;
        }

        public String getHostName() {
            return hostName;
        }

        public void setHostName(String hostName) {
            this.hostName = hostName;
        }

    }

    public static void main(String[] args) {

        System.out.println("Starting remote peers...");

        ArrayList<PeerInfo> peerList = new ArrayList<>();

        String ciseUser = "rangerchenore"; // change with your CISE username

        /**
         * Make sure the below peer hostnames and peerIDs match those in PeerInfo.cfg in
         * the remote CISE machines. Also make sure that the peers which have the file
         * initially have it under the 'peer_[peerID]' folder.
         */

        peerList.add(new PeerInfo("1001", "lin114-00.cise.ufl.edu"));
        peerList.add(new PeerInfo("1002", "lin114-01.cise.ufl.edu"));
        peerList.add(new PeerInfo("1003", "lin114-02.cise.ufl.edu"));
        peerList.add(new PeerInfo("1004", "lin114-03.cise.ufl.edu"));
        peerList.add(new PeerInfo("1005", "lin114-04.cise.ufl.edu"));

        for (PeerInfo remotePeer : peerList) {
            try {
                System.out.println("Starting peer: " + remotePeer.peerID);
                JSch jsch = new JSch();
                /*
                 * Give the path to your private key. Make sure your public key is already
                 * within your remote CISE machine to ssh into it without a password. Or you can
                 * use the corressponding method of JSch which accepts a password.
                 */
                jsch.addIdentity("~/.ssh/id_rsa", "");
                System.out.println("Getting session...");
                Session session = jsch.getSession(ciseUser, remotePeer.getHostName(), 22);
                System.out.println("Session obtained.");
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);

                System.out.println("Connecting...");
                session.connect();
                System.out.println("Connected.");

                System.out.println("Session to peer# " + remotePeer.getPeerID() + " at " + remotePeer.getHostName());

                Channel channel = session.openChannel("exec");
                System.out.println("remotePeerID" + remotePeer.getPeerID());
                ((ChannelExec) channel).setCommand(scriptPrefix + remotePeer.getPeerID());

                channel.setInputStream(null);
                ((ChannelExec) channel).setErrStream(System.err);

                InputStream input = channel.getInputStream();
                channel.connect();

                System.out.println("Channel Connected to peer# " + remotePeer.getPeerID() + " at "
                        + remotePeer.getHostName() + " server with commands");

                (new Thread() {
                    @Override
                    public void run() {

                        InputStreamReader inputReader = new InputStreamReader(input);
                        BufferedReader bufferedReader = new BufferedReader(inputReader);
                        String line = null;

                        try {

                            while ((line = bufferedReader.readLine()) != null) {
                                System.out.println(remotePeer.getPeerID() + ">:" + line);
                            }
                            bufferedReader.close();
                            inputReader.close();
                        } catch (Exception ex) {
                            System.out.println(remotePeer.getPeerID() + " Exception >:");
                            ex.printStackTrace();
                        }

                        channel.disconnect();
                        session.disconnect();
                    }
                }).start();
                System.out.println("Done with peer: " + remotePeer.peerID);
            } catch (JSchException e) {
                // TODO Auto-generated catch block
                System.out.println(remotePeer.getPeerID() + " JSchException >:");
                e.printStackTrace();
            } catch (IOException ex) {
                System.out.println(remotePeer.getPeerID() + " Exception >:");
                ex.printStackTrace();
            }

        }

        System.out.println("***Finished starting remote peers!");
    }

}
