# SMAnalyzer
SMAnalyzer was designed and built by Ben Ciummo, Kailee Calhoun, Amber Owings, Matteo Pezzino, Salvador Lopez Jr, and Lisa Wehrly.
This project will serve as their Senior Capstone project for the Washburn University CIS Program. 

SMAnalyzer (Social Media Analyzer) is an application that leverages APIs from various social media platforms, to provide detailed statistical and trend analyasis to the user.

The nature of this analysis is primarily focused on trimming down large groups of comments into prevalent topics, and providing more detail on each of those topics. The data is first rinsed of all non-English comments, troll posts, gibberish, rants, and instances of tagging. The rinsed comments are then flagged for different attributes like "IsEnglish", "IsFullName" etc. Each post is then analyzed for its positivity rating. The positivity rating is the total of explicitly positive or explicitly negative words contained in a post, summed together to result in an overall positive or negative score for each comment. Negative means the post overall contains a negative opinion or viewpoint relative to the original post, positive means and overall positive opinion or viewpoint relative to the original post.

As of right now, data can be skewed if the commenters are arguing with each other resulting in an overall negative score that may or may not accurately represent the comment source. Also, instance of facetiousness, sarcasm and other language nuances are not easily detected by the SMAnalyzer. This project may eventually be adapted to use AI principals and machine learning to improve detection accuracy. It was left open ended to allow for these features to be added.

Practical applications of this algorithm would involve its use in a social media marketing scenario where individuals do not necessarily administer a Facebook page, but still want details on that pages posts and responses. It could also be used to interpret survey data to identify common ideas within a large amount of comments be it social media data or not. Any individual who leverages data sourced from social media could potentially benefit from this application.
