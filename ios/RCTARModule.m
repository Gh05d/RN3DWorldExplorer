#import "RCTARModule.h"
#import <React/RCTLog.h>
#import <ModelIO/ModelIO.h>
#import <SceneKit/ModelIO.h>
#import <ARKit/ARKit.h>

@interface RCTARModule () <ARSCNViewDelegate>
@property (nonatomic, strong) ARSCNView *sceneView;
@property (nonatomic, strong) SCNNode *modelNode;
@end

@implementation RCTARModule

RCT_EXPORT_METHOD(showAR:(NSString *)filename)
{
  dispatch_async(dispatch_get_main_queue(), ^{
    RCTLogInfo(@"Loading model: %@", filename);
    [self initializeARView];
    [self loadAndDisplayModel:filename];
    [self presentARView];
  });
}

- (void)initializeARView {
  self.sceneView = [[ARSCNView alloc] initWithFrame:UIScreen.mainScreen.bounds];
  self.sceneView.delegate = self;
  
  // Configure AR session
  ARWorldTrackingConfiguration *configuration = [ARWorldTrackingConfiguration new];
  configuration.planeDetection = ARPlaneDetectionHorizontal;
  [self.sceneView.session runWithConfiguration:configuration];
}

- (void)loadAndDisplayModel:(NSString *)filename {
  NSString *filePath = [[NSBundle mainBundle] pathForResource:filename ofType:@"usdz"];
  NSURL *fileURL = [NSURL fileURLWithPath:filePath];
  
  NSError *error = nil;
  SCNScene *scene = [SCNScene sceneWithURL:fileURL options:nil error:&error];
  
  // Correctly set the modelNode property
  self.modelNode = [scene.rootNode.childNodes firstObject];
  if (self.modelNode) {
    self.modelNode.scale = SCNVector3Make(0.1, 0.1, 0.1);
    self.modelNode.position = SCNVector3Make(0, -1, -3); // Adjust as needed
    [self.sceneView.scene.rootNode addChildNode:self.modelNode];
  }
}

- (void)presentARView {
    UIViewController *viewController = [UIViewController new];
    [viewController.view addSubview:self.sceneView];
  
  // Create a close button
  UIButton *closeButton = [UIButton buttonWithType:UIButtonTypeSystem];
  [closeButton setTitle:@"X" forState:UIControlStateNormal];
  [closeButton addTarget:self action:@selector(closeARView) forControlEvents:UIControlEventTouchUpInside];
  
  CGFloat buttonSize = 44.0;
  CGFloat padding = 16.0;
  closeButton.frame = CGRectMake(viewController.view.bounds.size.width - buttonSize - padding,
     padding,
     buttonSize,
     buttonSize);
  closeButton.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleBottomMargin;
  closeButton.backgroundColor = [UIColor blueColor];
  closeButton.layer.cornerRadius = buttonSize / 2;
  closeButton.clipsToBounds = YES;

  [viewController.view addSubview:closeButton];

    // Assuming you have access to the root view controller or the current view controller
    UIViewController *rootViewController = RCTPresentedViewController();
    [rootViewController presentViewController:viewController animated:YES completion:nil];
}

- (void)closeARView {
    UIViewController *presentingController = self.sceneView.window.rootViewController;
    [presentingController dismissViewControllerAnimated:YES completion:nil];
}

RCT_EXPORT_MODULE();

@end
