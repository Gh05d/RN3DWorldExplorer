#import "RCTARModule.h"
#import <React/RCTLog.h>

@implementation RCTARModule

RCT_EXPORT_METHOD(showAR:(NSString *)location)
{
 RCTLogInfo(@"Pretending to load a file named %@", location);
}

RCT_EXPORT_MODULE();

@end
