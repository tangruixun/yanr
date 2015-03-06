package com.trx.yanr;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.StringTokenizer;

import org.apache.commons.net.nntp.ArticleInfo;
import org.apache.commons.net.nntp.NNTPClient;
import org.apache.commons.net.nntp.NewsgroupInfo;

public class NNTPMyClient {
	public static String getArticleSubject( Reader r ) {
		StringBuffer sb = new StringBuffer();
		    try {
		    	BufferedReader br = new BufferedReader( r );
			    String line = br.readLine();
			    while( line != null ) {
		    	if( line.indexOf( "Subject:" ) != -1 ) {
		    		return line.substring( line.indexOf( ':' ) + 1 );
		    		}
		        line = br.readLine();
			    }
		    }
		    catch( Exception e ) {
		    	e.printStackTrace();
		    }
		    return null;
	}

	public static String getArticleBody( Reader r ) {
		StringBuffer sb = new StringBuffer();
		try {
	    	BufferedReader br = new BufferedReader( r );
		    String line = br.readLine();
		    while( line != null ) {
		    	sb.append( line + "\n" );
		        line = br.readLine();
		    }
		    return sb.toString();
		}
		catch( Exception e ) {
		    	e.printStackTrace();
		    }
		    return null;
		}

		  
	public static ArticleInfo getFirstArticle( String replyString ) {
	    StringTokenizer st = new StringTokenizer( replyString );
	    int status = Integer.parseInt( st.nextToken() );
	    if( status == 211 ) {
	    	int articleCount = Integer.parseInt( st.nextToken() );
	    	int firstArticle = Integer.parseInt( st.nextToken() );
	    	int lastArticle = Integer.parseInt( st.nextToken() );
	    	String groupName = st.nextToken();

		    System.out.println( "\tGroup Name:  " + groupName );
		    System.out.println( "\tFirst Article: " + firstArticle );
		    System.out.println( "\tLast Article: " + lastArticle );
		    System.out.println( "\tArticle Count: " + articleCount );

		    ArticleInfo ap = new ArticleInfo();
		    ap.articleNumber = firstArticle;
		    return ap;
		}
		return null;
	}

	public static void main( String[] args ) {
		try {
			System.out.println( "Starting..." );
		    NNTPClient client = new NNTPClient();
		    System.out.println( "Connecting..." );
		    client.connect( "newsgroups.myserver.com", 119 );
		    System.out.println( "Connected to newsgroups.myserver.com, " +
		            "allowed to post = " + client.isAllowedToPost() );

		    // List all of the Newsgroups
		    NewsgroupInfo[] groups = client.listNewsgroups();
		    for( int i=0; i<groups.length; i++ ) {
		    System.out.println( groups[ i ].getNewsgroup() + 
		                  " (" + 
		                  groups[ i ].getArticleCount() + 
		                  "), range = " +
		                  groups[ i ].getFirstArticle() +
		                  " to " +
		                  groups[ i ].getLastArticle() +
		                  ", Posting = " +
		                  groups[ i ].getPostingPermission() );
		    }

		    // Connect to a Newsgroup and list its messages
		    boolean status = client.selectNewsgroup( "comp.lang.java" );
		    ArticleInfo ap = getFirstArticle( client.getReplyString() );
		    int firstArticle = (int) ap.articleNumber;
		    System.out.println( ap.articleNumber + ": " + 
		    		getArticleSubject( client.retrieveArticleHeader( 
		    				ap.articleNumber ) ) );
		    while( client.selectNextArticle( ap ) ) {
		    	System.out.println( ap.articleNumber + ": " + 
		    			getArticleSubject( client.retrieveArticleHeader( 
		    					ap.articleNumber ) ) );
		    }

		    // Display the body of the last message in the group
		    String body = getArticleBody( 
		    		client.retrieveArticleBody( firstArticle ) );
		    System.out.println( 
		    		"Article Body for first article in group: \n" + body );

		    client.logout();
		    client.disconnect();
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}
}
